//package com.github.beafland.fallofbastille.character;
//
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.image.Image;
//
//import java.util.Objects;
//
//public class Mage extends Player{
//    //初始化角色图片，不知道为什么一定要这么写要不然找不到图片
//    private static final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanician.png")));
//    private static final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianMove.gif")));
//    private static final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/gunFire.png")));
//    private static final int attackRange = 150;
//    private Image playerCurrImage;
//
//    private double x;
//    private double y;
//
//    public Mage(int x, int y) {
//        super(x, y, 100);
//        this.playerCurrImage = playerStandImage;
//    }
//
//    @Override
//    public void render(GraphicsContext gc) {
//        if (!facingLeft) {
//            gc.save(); // 保存当前画布状态
//            gc.translate(x + WIDTH, y); // 将绘制起点向右移动图像宽度
//            gc.scale(-1, 1); // 水平翻转
//            gc.drawImage(playerCurrImage, 0, 0, WIDTH, HEIGHT); // 从新的原点绘制
//            gc.restore(); // 恢复画布状态到最近的保存点
//        } else {
//            // 如果面朝左边，正常绘制
//            gc.drawImage(playerCurrImage, x, y, WIDTH, HEIGHT);
//        }
//
//        if (isFire) {
//            // 根据玩家角色的朝向决定是否进行水平翻转
//            if (!facingLeft) {
//                gc.save(); // 保存当前画布状态
//                gc.translate(x + WIDTH / 2, y + HEIGHT / 2); // 将绘制起点移动到攻击位置
//                gc.scale(-1, 1); // 水平翻转
//                gc.drawImage(gunFire, 0, 0, attackRange, 75); // 从新的原点绘制
//                gc.restore(); // 恢复画布状态到最近的保存点
//            } else {
//                gc.drawImage(gunFire, x - WIDTH / 2, y + HEIGHT / 3, attackRange, 75);
//            }
//        }
//    }
//}
