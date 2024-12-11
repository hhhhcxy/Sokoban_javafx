package org.view.LevelSelect;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.model.*;
import org.view.level.Grass;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class Cat {
    Image cat_stand = new Image(getClass().getResourceAsStream("/images/player_cat/cat_stand.gif"), config.Map_Node_Width, config.Map_Node_Width, false, false);
    Image cat_run = new Image(getClass().getResourceAsStream("/images/player_cat/cat_run.gif"), config.Map_Node_Width, config.Map_Node_Width, false, false);
    Image cat_run_front = new Image(getClass().getResourceAsStream("/images/player_cat/cat_run_front.gif"), config.Map_Node_Width, config.Map_Node_Width, false, false);
    Image cat_run_back = new Image(getClass().getResourceAsStream("/images/player_cat/cat_run_back.gif"), config.Map_Node_Width, config.Map_Node_Width, false, false);
    Image cat_stand_front = new Image(getClass().getResourceAsStream("/images/player_cat/cat_stand_front.gif"), config.Map_Node_Width, config.Map_Node_Width, false, false);
    Image cat_stand_back = new Image(getClass().getResourceAsStream("/images/player_cat/cat_stand_back.gif"), config.Map_Node_Width, config.Map_Node_Width, false, false);
    ImageView imageView;
    int x, y;
    public Cat() {
        imageView = new ImageView(cat_stand);
        imageView.setFitHeight(config.Map_Node_Width);
        imageView.setFitWidth(config.Map_Node_Width);
        x = 0; y = 0;
    }
    boolean is_moving = false;
    public void move(char dir){ // 0 : right 1:down 2:up
        imageView.setFitHeight(config.Map_Node_Width); imageView.setFitWidth(config.Map_Node_Width);

//        Timeline move_timeline = new Timeline(
//                new KeyFrame(Duration.ZERO,
//                        new KeyValue(imageView.layoutXProperty(), imageView.getLayoutX()),
//                        new KeyValue(imageView.layoutYProperty(), imageView.getLayoutY())),
//                new KeyFrame(Duration.millis(config.move_anim_duration),
//                        new KeyValue(imageView.layoutXProperty(), imageView.getLayoutX() + dx),
//                        new KeyValue(imageView.layoutYProperty(), imageView.getLayoutY() + dy))
//        );

        TranslateTransition tt = new TranslateTransition(Duration.millis(config.move_anim_duration), imageView);
        if(dir == 'd') {
            imageView.setImage(cat_run); imageView.setScaleX(1);
            tt.setFromX(-config.Map_Node_Width);
            tt.setToX(0); x++;
        } else if (dir == 's'){
            imageView.setImage(cat_run_front);
            tt.setFromY(-config.Map_Node_Width);
            tt.setToY(0);y++;
        } else if (dir == 'w'){
            imageView.setImage(cat_run_back);
            tt.setFromY(config.Map_Node_Width);
            tt.setToY(0); y--;
        } else if (dir == 'a'){
            imageView.setImage(cat_run); imageView.setScaleX(-1);
            tt.setFromX(config.Map_Node_Width); x--;
            tt.setToX(0);
        }
        tt.setOnFinished(e -> {
            if(dir == 'd') {imageView.setImage(cat_stand); imageView.setScaleX(1);}
            else if(dir == 's') imageView.setImage(cat_stand_front);
            else if(dir == 'w') imageView.setImage(cat_stand_back);
            else if(dir == 'a') {imageView.setImage(cat_stand); imageView.setScaleX(-1);}
            imageView.setFitHeight(config.Map_Node_Width); imageView.setFitWidth(config.Map_Node_Width);
            is_moving = false;
            imageView.setTranslateX(0); imageView.setTranslateY(0);
            imageView.setLayoutX(SelectMap.AnchorX + x * config.Map_Node_Width);
            imageView.setLayoutY(SelectMap.AnchorY + y * config.Map_Node_Width);
        });
        is_moving = true;
        tt.play();
        tt.setCycleCount(1);
    }
}


public class SelectMap {
    static double AnchorX, AnchorY;
    private Stage stage;
    private Scene scene;
    private Pane root;
    private Cat cat;
    private HashMap<Coordinate, Integer> map;

    private ArrayList<MapNode> nodes;

    public SelectMap(Stage stage) {
        this.stage = stage;
        cat = new Cat();
        AnchorX = 100; AnchorY = 100;
        if(stage.getScene() != null){
            this.scene = stage.getScene();
            this.root = (Pane) scene.getRoot();
        } else {
            this.root = new Pane();
            this.scene = new Scene(root);
        }
    }

    public void add_levels(int[][][] maps, User user){
        MapNode.maps = maps;
        for(int i = 0; i < maps.length; i++){
            MapNode node = new MapNode(i, stage);
            node.target_level = i;
            if(user.getLevelAt() < i) node.is_locked = true;
            if(nodes == null) nodes = new ArrayList<>();
            nodes.add(node);
        }
        map = new HashMap<>();
        map.put(new Coordinate(1,1),1);
    }

    public void draw() {
        root.getChildren().clear(); root.setStyle("-fx-background-color: #FFFFFF");
        scene.getStylesheets().add("file://" + new java.io.File("./src/main/resources/css/styles.css").getAbsolutePath());
        root.setLayoutX(0); root.setLayoutY(0); // 设置根节点的位置

        //画背景
        Rectangle background = new Rectangle(0,0,config.ScreenWidth, config.ScreenHeight);
        background.setFill(Color.WHITE);
        root.getChildren().add(background);

//        // 画障碍
//        for(int i = 0; i < map.length; i++){
//            for(int j = 0; j < map[0].length; j++){
//                if(map[i][j] == -1){
//                    Rectangle rect = new Rectangle(AnchorX + i * config.Map_Node_Width, AnchorY + j * config.Map_Node_Width, config.Map_Node_Width, config.Map_Node_Width);
//                    rect.setFill(Color.BLACK);
//                    root.getChildren().add(rect);
//                }
//            }
//        }

        // 画goals
        for(MapNode node: nodes) {
            node.button.setLayoutX(AnchorX + node.x * config.Map_Node_Width);
            node.button.setLayoutY(AnchorY + node.y * config.Map_Node_Width);
            root.getChildren().add(node.button);
        }
        // 画猫
        cat.imageView.setLayoutX(AnchorX + cat.x * config.Map_Node_Width);
        cat.imageView.setLayoutY(AnchorY + cat.y * config.Map_Node_Width);
        root.getChildren().add(cat.imageView);
    }

    Timeline cameraTimeline = null;

    public void Move() {
        cameraTimeline = new Timeline(new KeyFrame(Duration.seconds(0.03), e -> {
            // 得到画面中心的坐标
            double midx = stage.getWidth() / 2 - (double) config.Map_Node_Width / 2;
            double midy = stage.getHeight() / 2 - (double) config.Map_Node_Width / 2;
            // 得到中心的坐标
            double catx = cat.imageView.getLayoutX() + config.Map_Node_Width / 2;
            double caty = cat.imageView.getLayoutY() + config.Map_Node_Width/ 2;
            double dx = midx - catx, dy = midy - caty;
            int mid_dis = 1;
            if(dx < -config.Map_Node_Width * mid_dis) dx += config.Map_Node_Width * mid_dis;
            else if(dx > config.Map_Node_Width * mid_dis) dx -= config.Map_Node_Width * mid_dis;
            else dx = 0;
            if(dy < -config.Map_Node_Width * mid_dis) dy += config.Map_Node_Width * mid_dis;
            else if(dy > config.Map_Node_Width * mid_dis) dy -= config.Map_Node_Width * mid_dis;
            else dy = 0;
            //if(Math.abs(dx) < 10 && Math.abs(dy) < 10 && cameraTimeline != null) cameraTimeline.stop();
            // 移动画面，使人物在中间
            AnchorX += dx / 30; AnchorY += dy / 30;
            draw();
        }));
        config.timelines.add(cameraTimeline);
        cameraTimeline.setCycleCount(Timeline.INDEFINITE);
        cameraTimeline.play();
    }

    public void update() {
        draw();
        Move();

        scene.setOnMouseClicked(event -> {
           // if(event.getClickCount() == 1) return;
            int x = (int) ((event.getSceneX() - AnchorX) / config.Map_Node_Width);
            int y = (int) ((event.getSceneY() - AnchorY) / config.Map_Node_Width);
            final String[] moves = {FindPath.findPath(map, new Coordinate(cat.x, cat.y), new Coordinate(x, y))};

            // 创建一个时间轴，每一段时间触发一次事件
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(config.move_anim_duration ), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!cat.is_moving && !moves[0].isEmpty()) {
                        char dir = moves[0].charAt(0);
                        moves[0] = moves[0].substring(1);
                        cat.move(dir);
                    }
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        });

    }

    public Scene getScene() {
        return scene;
    }
}
