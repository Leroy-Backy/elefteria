export class StaticMethods{

  public static formatToDate(date: Date): Date {
    date = new Date(date)

    date.setUTCFullYear(date.getFullYear())
    date.setUTCMonth(date.getMonth())
    date.setUTCDate(date.getDate())
    date.setUTCHours(date.getHours())
    date.setUTCMinutes(date.getMinutes())
    date.setUTCSeconds(date.getSeconds())

    return date;
  }
}
