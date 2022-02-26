import {FormControl, FormGroup, ValidationErrors} from "@angular/forms";

export class MyValidators {

  static fieldMatch(controlFiled: string, matchingControlField: string){
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlFiled];
      const matchingControl = formGroup.controls[matchingControlField]

      if(control.value !== matchingControl.value)
        matchingControl.setErrors({fieldMatch: true})
      else
        matchingControl.setErrors(null)
    }
  }

  static notBlank(control: FormControl): ValidationErrors{
    if((control.value != null) && control.value.trim().length === 0){
      return {"notBlank": true}
    }
    else {
      // @ts-ignore
      return false;
    }
  }
}
