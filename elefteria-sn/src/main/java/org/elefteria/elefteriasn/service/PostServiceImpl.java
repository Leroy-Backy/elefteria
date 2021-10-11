package org.elefteria.elefteriasn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elefteria.elefteriasn.dao.LikeRepository;
import org.elefteria.elefteriasn.dao.PollRepository;
import org.elefteria.elefteriasn.dao.PostRepository;
import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.dto.PollDto;
import org.elefteria.elefteriasn.dto.PollOptionDto;
import org.elefteria.elefteriasn.dto.PostDto;
import org.elefteria.elefteriasn.entity.*;
import org.elefteria.elefteriasn.exception.MyEntityNotFoundException;
import org.elefteria.elefteriasn.exception.MyForbiddenException;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService{

    private PostRepository postRepository;
    private UserRepository userRepository;
    private LikeRepository likeRepository;
    private PollRepository pollRepository;

    private FileService fileService;
    private NotificationService notificationService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           LikeRepository likeRepository,
                           FileService fileService,
                           PollRepository pollRepository,
                           NotificationService notificationService
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.fileService = fileService;
        this.pollRepository = pollRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Page<PostDto> getPostsFeed(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();

        Page<Post> postPage = postRepository.findPostsForFeedByUserId(user.getId(), pageable);

        Page<PostDto> postDtoPage = postPage.map(PostDto::formatToPostDto);

        return postDtoPage;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> createPost(PostDto postDto) {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        Post post = new Post(postDto.getTitle(), postDto.getText());
        post.setAmountOfLikes(0);

        MultipartFile[] files = postDto.getFiles();
        if(files != null){
            for(MultipartFile file: files){
                String fileName = fileService.uploadImageToLocalFileSystem(file);

                Image image = new Image(fileName);
                post.addImage(image);
            }
        }

        post.setUser(user);

        if(postDto.getPollString() != null){

            ObjectMapper objectMapper = new ObjectMapper();

            PollDto pollDto = new PollDto();

            try {
                pollDto = objectMapper.readValue(postDto.getPollString(), PollDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Can't serialize poll!");
            }

            Poll poll = new Poll(pollDto.getQuestion());

            int number = 0;

            for(PollOptionDto optDto: pollDto.getOptions())
                poll.addOption(new PollOption(optDto.getOption(), ++number));

            post.setPoll(poll);

            poll.setPost(post
            );
        }

        postRepository.save(post);


        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "Post was successfully created",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public Page<PostDto> getPostsByUserId(Long id, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedDateDesc(id, pageable);

        if(posts.getNumberOfElements() == 0)
            throw new MyEntityNotFoundException("Not found posts for user with id: " + id);

        Page<PostDto> postDtos = posts.map(PostDto::formatToPostDto);

        return postDtos;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> likePost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isEmpty())
            throw new MyEntityNotFoundException("Post not found with id: " + postId);

        Post post = postOptional.get();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Like> likeOptional = likeRepository.findByPostIdAndUsername(postId, username);
        String message;

        int amount = likeRepository.countByPostId(postId);

        if(likeOptional.isEmpty()){
            message = "liked";
            Like like = new Like(username);
            post.addLike(like);
            post.setAmountOfLikes(amount + 1);
            likeRepository.save(like);
            notificationService.createLikeNotification(username, post);
        } else {
            message = "unliked";
            Like like = likeOptional.get();
            post.removeLike(like);
            post.setAmountOfLikes(amount - 1);
            likeRepository.delete(like);
        }

        postRepository.save(post);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                message,
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public Page<PostDto> getPopularPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.getPostFromLast7DaysOrderByLikesPlusComments(pageable);

        Page<PostDto> postDtoPage = postPage.map(PostDto::formatToPostDto);

        return postDtoPage;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> deletePost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isEmpty())
            throw new MyEntityNotFoundException("Post not found with id: " + postId);

        Post post = postOptional.get();
        boolean admin = false;

        for(GrantedAuthority authority: SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
            if(authority.getAuthority().equals("ROLE_ADMIN"))
                admin = true;
        }

        boolean current = SecurityContextHolder.getContext().getAuthentication().getName().equals(post.getUser().getUsername());

        if(!(current || admin))
            throw new MyForbiddenException("You have not access!");

        if(post.getImages() != null && post.getImages().size() > 0){
            for(Image image: post.getImages()){
                fileService.deleteImageByFileName(image.getFileName());
            }
        }

        postRepository.delete(post);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "Post was successfully deleted",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> voteInPoll(Long pollId, Long optionId) {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        Optional<Poll> pollOptional = pollRepository.findById(pollId);
        if(pollOptional.isEmpty())
            throw new MyEntityNotFoundException("Poll not found with id: " + pollId);

        Poll poll = pollOptional.get();

        boolean optionIsPresent = false;

        PollOption option = null;

        for(PollOption tempOption: poll.getOptions()){
            if(tempOption.getNumber() == optionId) {
                optionIsPresent = true;
                option = tempOption;
            }

            for(User tempUser: tempOption.getVotes()){
                if(tempUser.getUsername().equals(user.getUsername()))
                    throw new RuntimeException("User already voted");
            }
        }

        if(!optionIsPresent)
            throw new MyEntityNotFoundException("Poll option not found with id: " + optionId);

        option.addVote(user);

        pollRepository.save(poll);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "User successfully voted",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public Set<String> getLikesByPostId(Long postId) {
        return likeRepository.getLikesUsernameByPostId(postId);
    }

    @Override
    @Transactional
    public PostDto getLastPostForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Post> postOptional = postRepository.findTop1ByUserUsernameOrderByCreatedDateDesc(username);

        if(postOptional.isEmpty())
            throw new MyEntityNotFoundException("Not found posts for user");

        return new PostDto(postOptional.get());
    }

    @Override
    @Transactional
    public PostDto getPostById(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);

        if(postOptional.isEmpty())
            throw new MyEntityNotFoundException("Post not found with id " + id);

        return new PostDto(postOptional.get());
    }
}














