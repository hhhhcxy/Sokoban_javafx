package org.view.level;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.data.mapdata;
import org.model.MapMatrix;
import org.model.Solve.Solve;
import org.model.User;
import org.model.config;
import org.view.VisualEffects.GlowRectangle;
import org.view.game.box;
import org.view.game.player;

import java.util.ArrayList;
import java.util.Map;

public class FightLevel extends Level {

    private int id;
    private boolean reverse = false;


    public MapMatrix getMap() {
        return (MapMatrix) map;
    }
    org.view.game.player player1, player2;

    public FightLevel(Pane root, int id, Stage primaryStage, User user, boolean reverse) {
        super(root, primaryStage, user);
        this.id = id;
        this.reverse = reverse;
        init();
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    @Override
    public void init() {
        //将 mapdata.maps[id] 对称复制一份，然后赋值给 map
        int[][] doubleMap = new int[mapdata.maps[id].length][mapdata.maps[id][0].length * 2 + 1];
        for(int i = 0; i < mapdata.maps[id].length; i++){
            for(int j = 0; j < mapdata.maps[id][0].length; j++){
                doubleMap[i][j] = mapdata.maps[id][i][j];
                doubleMap[i][mapdata.maps[id][0].length * 2 - j] = mapdata.maps[id][i][j];
            }
        }
        map = new MapMatrix(doubleMap);
        if(canvas == null)
            canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int boxIndex = 1; // 从1开始编号
        boxes = new ArrayList<>();

        double width = primaryStage.getWidth();
        double height = primaryStage.getHeight();

        setAnchor_posx((width - map.getWidth() * config.tile_size)/2);
        setAnchor_posy((height - map.getHeight() * config.tile_size)/2);
        player1 = null;
        player2 = null;
        for (int y = 0; y < map.getHeight(); ++y) {
            for (int x = 0; x < map.getWidth(); ++x) {
                if (map.hasBox(x, y)) {
                    box temp = new box(x, y, boxIndex++);
                    temp.getImageView().setX(anchor_posx + x * config.tile_size);
                    temp.getImageView().setY(anchor_posy + (y - config.box_angle_amount) * config.tile_size);
                    boxes.add(temp);
                }
                if (map.hasPlayer(x, y)) {
                    if(player1 == null){
                        player1 = new player(x, y, primaryStage, true);
                        player1.getImageView().setX(anchor_posx + x * config.tile_size);
                        player1.getImageView().setY(anchor_posy + y * config.tile_size);
                    }else{
                        player2 = new player(x, y, primaryStage, true);
                        player2.getImageView().setX(anchor_posx + x * config.tile_size);
                        player2.getImageView().setY(anchor_posy + y * config.tile_size);
                    }
                }
            }
            //取消 player1 和 player2 的 cameraTimeline
            if(player1 != null) player1.stopCameraTimeline();
            if(player2 != null) player2.stopCameraTimeline();
        }
        generate_glow_rects();
        createButterflyTimeline();
        load_gui(user);
        fadeRectangle = new Rectangle(primaryStage.getWidth(), primaryStage.getHeight(), Color.BLACK);
        fadeRectangle.setX(0);
        fadeRectangle.setY(0);
        fadeRectangle.setOpacity(1.0);
        root.getChildren().add(fadeRectangle);
        createFadeTimeline();
        drawMap();
    }

    @Override
    public void drawMap() {
        root.getChildren().clear(); // 先清空一下地图
        drawGrass();
        drawButterflyShadow();
        drawBackGround();
//        drawBoxes();
        drawPlayer();
        drawBoxesAndWall();
        drawButterfly();
        root.getChildren().add(fadeRectangle);
    }
    public void drawPlayer() {
        root.getChildren().add(player1.getImageView());
        root.getChildren().add(player2.getImageView());
    }

    public int oneIsWin(){
        boolean player1Win = true, player2Win = true;
        for(int y = 0; y < map.getHeight(); ++y){
            for(int x = 0; x < map.getWidth() / 2; ++x){
                if(map.hasGoal(x, y) && !map.hasBox(x, y)){
                    player1Win = false;
                    break;
                }
            }
        }
        for(int y = 0; y < map.getHeight(); ++y){
            for(int x = map.getWidth() / 2; x < map.getWidth(); ++x){
                if(map.hasGoal(x, y) && !map.hasBox(x, y)){
                    player2Win = false;
                    break;
                }
            }
        }
        if(player1Win) return 1;
        if(player2Win) return 2;
        return 0;
    }
    public String solve_moves(MapMatrix map) {
        solve = new Solve(map);
        if(!config.auto_check_fail && !config.this_is_hint)
            return solve.simple_search()? "N":" ";
        return solve.aStarSearch();
    }

    public boolean checkDraw() {
        int[][] leftMap = new int[mapdata.maps[id].length][mapdata.maps[id][0].length];
        int[][] rightMap = new int[mapdata.maps[id].length][mapdata.maps[id][0].length];
        for(int y = 0; y < map.getHeight(); ++y){
            for(int x = 0; x < map.getWidth() / 2; ++x){
                leftMap[y][x] = map.get(x, y);
            }
        }
        for(int y = 0; y < map.getHeight(); ++y){
            for(int x = map.getWidth() / 2 + 1; x < map.getWidth(); x++){
                rightMap[y][x - map.getWidth() / 2 - 1] = map.get(x, y);
            }
        }
//        if(true) return false;
        MapMatrix lMap = new MapMatrix(leftMap);
        MapMatrix rMap = new MapMatrix(rightMap);
        //输出左右两个地图
//        System.out.println("leftMap:");
//        for (int y = 0; y < lMap.getHeight(); ++y) {
//            for (int x = 0; x < lMap.getWidth(); ++x) {
//                System.out.print(lMap.get(x, y) + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("rightMap:");
//        for (int y = 0; y < rMap.getHeight(); ++y) {
//            for (int x = 0; x < rMap.getWidth(); ++x) {
//                System.out.print(rMap.get(x, y) + " ");
//            }
//            System.out.println();
//        }
        boolean originData = config.auto_check_fail;
        config.auto_check_fail = false;
        String leftSolve = solve_moves(lMap);
        String rightSolve = solve_moves(rMap);
        config.auto_check_fail = originData;
        if(leftSolve.charAt(0) == 'N' && rightSolve.charAt(0) == 'N') return true;
        else return false;
    }


}
