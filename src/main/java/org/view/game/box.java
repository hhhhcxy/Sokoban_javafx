package org.view.game;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.model.MapMatrix;
import org.model.config;

public class box extends entity {
    public int id;
    public box(int x, int y, int id) {
        super(x, y, 1);
        this.id = id;
        image = new Image(getClass().getResourceAsStream("/images/box.png"));
        imageView = new ImageView(image);
        imageView.setFitHeight(config.tile_size);
        imageView.setFitWidth(config.tile_size);
    }

    @Override
    public void move(MapMatrix map) {
        if (can_move(map, velocity_x, velocity_y)) {
            // 动画
            imageView.setX(imageView.getX() + velocity_x * config.tile_size);
            imageView.setY(imageView.getY() + velocity_y * config.tile_size);
            TranslateTransition transition = new TranslateTransition(Duration.millis(config.move_anim_duration), imageView);
            transition.setFromX(-velocity_x * config.tile_size);
            transition.setFromY(-velocity_y * config.tile_size); // 用的似乎是相对坐标

            map.remove(x, y, type);
            map.setBox_matrix(x, y,0); // 把原来位置的箱子干掉
            x += velocity_x;
            y += velocity_y;
            map.add(x, y, type); // 向那一格第i位加入
            map.setBox_matrix(x, y, id); // 把新的箱子放进去

            transition.setToX(0);
            transition.setToY(0);
            transition.setOnFinished(e -> {
                imageView.setTranslateX(0); // 重置 translateX，因为动画已经结束
                imageView.setTranslateY(0); // 重置 translateY，因为动画已经结束
            });
            transition.play();
        }
    }

    public Image getImage() {
        return image;
    }
    public ImageView getImageView() {
        return imageView;
    }

}
