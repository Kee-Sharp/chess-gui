import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class returning an ImageView to allow for multiply copies of the same image
 * to be added to a GridPane
 * @author skee8
 * @version 1.0
 */
public class MyImageView {
    private Image image;
    /**
     * creates an instance with a contained image
     * @param  i the Image to be used for each ImageView
     */
    public MyImageView(Image i) {
        image = i;
    }
    /**
     * makes a new ImageView with the image field
     * @return that view
     */
    public ImageView use() {
        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        view.setFitHeight(15);
        return view;
    }
}