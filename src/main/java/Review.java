import java.util.List;

/**
 * Created by United121 on 10/23/2016.
 */
public class Review {
    private String url;
    private String review;
    private String rating;
    private List<String> plus;
    private List<String> minus;
    private int usefulReview;
    private int uselessReview;
    private String date;
    private String nameShop;

    public Review(String url, String review, String rating, List<String> plus, List<String> minus, int usefulReview, int uselessReview, String date, String nameShop) {
        this.url = url;
        this.review = review;
        this.rating = rating;
        this.plus = plus;
        this.minus = minus;
        this.usefulReview = usefulReview;
        this.uselessReview = uselessReview;
        this.date = date;
        this.nameShop = nameShop;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPlus(List<String> plus) {
        this.plus = plus;
    }

    public void setMinus(List<String> minus) {
        this.minus = minus;
    }

    public String getReview() {
        return review;
    }

    public int getRating() {
        try {
            return Integer.parseInt(rating.substring(rating.contains("%") ? 0 : rating.indexOf("%")));
        } catch (NumberFormatException | StringIndexOutOfBoundsException e){
            return 0;
        }
    }

    public String getRatingString() {
        return this.rating;
    }

    public List<String> getPlus() {
        return plus;
    }

    public List<String> getMinus() {
        return minus;
    }

    public int getUsefulReview() {
        return usefulReview;
    }

    public void setUsefulReview(int usefulReview) {
        this.usefulReview = usefulReview;
    }

    public int getUselessReview() {
        return uselessReview;
    }

    public void setUselessReview(int uselessReview) {
        this.uselessReview = uselessReview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNameShop() {
        return nameShop;
    }

    public void setNameShop(String nameShop) {
        this.nameShop = nameShop;
    }

    @Override
    public String toString() {
        return "Review{" +
                "url='" + url + '\'' +
                ", review='" + review + '\'' +
                ", rating='" + rating + '\'' +
                ", plus=" + plus +
                ", minus=" + minus +
                ", usefulReview=" + usefulReview +
                ", uselessReview=" + uselessReview +
                ", date='" + date + '\'' +
                ", nameShop='" + nameShop + '\'' +
                '}';
    }
}
