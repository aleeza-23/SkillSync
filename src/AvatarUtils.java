import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.ByteArrayInputStream;

public final class AvatarUtils {

    private AvatarUtils() {
    }

    public static void applyAvatar(ImageView imageView, byte[] profilePicture) {
        Image image = buildImage(profilePicture);
        imageView.setImage(image);
    }

    private static Image buildImage(byte[] profilePicture) {
        if (profilePicture != null && profilePicture.length > 0) {
            try {
                return new Image(new ByteArrayInputStream(profilePicture));
            } catch (Exception ignored) {
            }
        }
        return createDefaultAvatar();
    }

    private static Image createDefaultAvatar() {
        int size = 120;
        WritableImage image = new WritableImage(size, size);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - (size / 2.0);
                double dy = y - (size / 2.0);
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= (size / 2.0)) {
                    pixelWriter.setColor(x, y, Color.web("#dfe6e9"));
                } else {
                    pixelWriter.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        return image;
    }
}
