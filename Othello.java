import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.text.*;
import javafx.scene.*;
import javafx.event.*;
import javafx.geometry.Insets;

public class Othello extends Application{
    private static final int WIDTH = 320;
    private static final int HEIGHT = 320;
    private static final int GRID_X = 8;
    public static final int GRID_Y = 8;
    public static final int GRID_SIZE = 40;
    public int tebanFlg;
    public Text text;
    GraphicsContext g;
    GridInfo gf = new GridInfo(GRID_X, GRID_Y);

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start (Stage stage)throws Exception{
        
        BorderPane bp = new BorderPane();
        Canvas cvs = new Canvas(WIDTH, HEIGHT);
        this.g = cvs.getGraphicsContext2D();
        text = new Text("〇白の番です");
        bp.setTop(cvs);
        bp.setBottom(text);
        Scene sc = new Scene(bp, WIDTH, 360, Color.GREEN);
        sc.getStylesheets().add("style.css");
        text.getStyleClass().add("text");
        stage.setScene(sc);

        stage.setTitle("Othello");
        stage.setResizable(false);
        stage.show();

        sc.setOnMouseClicked(this::mouseClicked);

        g.setStroke(Color.BLACK);
        for(int loopY = 0; loopY < GRID_Y; loopY++){
            g.strokeLine(0, loopY * GRID_SIZE, WIDTH, loopY * GRID_SIZE);   //横線
        }
        for(int loopX = 0; loopX < GRID_X; loopX++){
            g.strokeLine(loopX * GRID_SIZE, 0, loopX *GRID_SIZE, HEIGHT);  //縦線
        }
    
        for(int loopY =0; loopY < GRID_Y; loopY++){
            for(int loopX = 0; loopX < GRID_X; loopX++){
                g.setFill(gf.getGridStoneColor(loopX, loopY));//塗りつぶす色を指定
                g.fillOval(loopX * GRID_SIZE + 5, loopY * GRID_SIZE + 5, 30, 30);
            }
        }
    }

    //描画処理
    public void mouseClicked(MouseEvent e){
        double clickPosX = 0;
        double clickPosY = 0;
        int clickTileX = 0;
        int clickTileY = 0;

        clickPosX = e.getX();
        clickPosY = e.getY();

        //クリックされた座標からクリックされた石を置く横マスを取得
        for(int loop = 0; loop < GRID_X; loop++){
            if(loop * GRID_SIZE <= clickPosX && clickPosX < (loop + 1) * GRID_SIZE){
                clickTileX = loop;
                break;
            }
        }

        //クリック座標からクリックされた石を置く縦マスを取得
        for(int loop = 0; loop < GRID_Y; loop++){
            if(loop * GRID_SIZE <= clickPosY && clickPosY < (loop + 1) * GRID_SIZE){
                clickTileY = loop;
                break;
            }
        }
        //石の初期配置をセット
        gf.reverseGridStateFlg(clickTileX, clickTileY);
        for(int loopY = 0; loopY < GRID_Y; loopY++){
            for(int loopX = 0; loopX < GRID_X; loopX++){
                g.setFill(gf.getGridStoneColor(loopX, loopY));
                g.fillOval(loopX * GRID_SIZE + 5, loopY * GRID_SIZE + 5, 30, 30);
            }
        }
        //グレー
        for(int loopY = 0; loopY < GRID_Y; loopY++){
            for(int loopX = 0; loopX < GRID_X; loopX++){
                gf.resetGrayStone(loopX, loopY);
                gf.chkCanSetStone(loopX, loopY);//GRAY_STATEを埋める
                g.setFill(gf.getGrayStoneColor(loopX, loopY));
                g.fillOval(loopY * GRID_SIZE + 12.5, loopX * GRID_SIZE + 12.5, 15, 15);
            }
        }
        tebanFlg = gf.getTebanFlg();
        if(tebanFlg == gf.BLACK_STATE){
            text.setText("●黒の番です");
        }else{
            text.setText("○白の番です");
        }
    }
}
class GridInfo{
    public final int NON_STATE = 0;
    public final int BLACK_STATE = 1;
    public final int WHITE_STATE = 2;
    public final int GRAY_STATE = 3;
    private int GRID_X;
    private int GRID_Y;
    private int gridStateFlg[][];
    private int gridStateFlgGray[][];
    private int tebanFlg;

    //コンストラクタ
    GridInfo(int gridXCnt, int gridYCnt){
        GRID_X = gridXCnt;
        GRID_Y = gridYCnt;
        gridStateFlg = new int[GRID_X][GRID_Y];
        gridStateFlgGray = new int[GRID_X][GRID_Y];
        for(int loopY = 0; loopY < gridYCnt; loopY++){
            for(int loopX  = 0; loopX < gridXCnt; loopX++){
                gridStateFlg[loopY][loopX] = NON_STATE;  
            }
        }
        //石の初期配置をセット
        gridStateFlg[3][3] = WHITE_STATE;
        gridStateFlg[4][4] = WHITE_STATE;
        gridStateFlg[4][3] = BLACK_STATE;
        gridStateFlg[3][4] = BLACK_STATE;
        //先手を黒に設定
        tebanFlg = WHITE_STATE;
        System.out.println("白の番です");
    }
    //指定したマスに置かれる石をセットするメソッド（初手？）
    public void setGridStateFlg(int x, int y){
        if(tebanFlg == BLACK_STATE){
            gridStateFlg[y][x] = BLACK_STATE;
            tebanFlg = WHITE_STATE;
        }else if(tebanFlg == WHITE_STATE){
            gridStateFlg[y][x] = WHITE_STATE;
            tebanFlg = BLACK_STATE;
        }else{
            gridStateFlg[y][x] = NON_STATE;
        }
    }
    //指定したマスに置かれる石をセットするメソッド（２手目以降？）
    public void setGridStateFlg(int x, int y, int ownColor){
        if(ownColor == BLACK_STATE){
            gridStateFlg[y][x] = BLACK_STATE;
        }else if(ownColor == WHITE_STATE){
            gridStateFlg[y][x] = WHITE_STATE;
        }else{
            gridStateFlg[y][x] = NON_STATE;
        }
    }
    //指定したマスに置かれる石を取得するメソッド
    public int getGridStateFlg(int x, int y){
        return gridStateFlg[y][x];
    }
    //
    public int getGridStateFlgGray(int x, int y){
        return gridStateFlgGray[y][x];
    }
    //
    public Color getGrayStoneColor(int x, int y){
        if(gridStateFlgGray[y][x] == GRAY_STATE){
              return Color.GRAY;
        }else{
            return null;
        }
    }
    //指定したマスに置かれている石の色を取得するメソッド
    public Color getGridStoneColor(int x, int y){
        switch(gridStateFlg[y][x]){
            case BLACK_STATE:
              return Color.BLACK;
            case WHITE_STATE:
              return Color.WHITE;
            default:
              return Color.GREEN;
        }
    }
    //tebanFlgを取得
    public int getTebanFlg(){
        return tebanFlg;
    }
    //reset Gray Stone
    public void resetGrayStone(int gridX,int gridY){
        gridStateFlgGray[gridY][gridX] = NON_STATE;
    }

    //石を置ける位置をチェックするメソッド
    public int chkCanSetStone(int gridX, int gridY){
        boolean blnRet;
        int ownColor;
        int revColor;
        int GrayCnt = 0;
        int leftGrayCnt = 0;
        int leftTopGrayCnt = 0;
        int topGrayCnt = 0;
        int rightGrayCnt = 0;
        int rightTopGrayCnt = 0;
        int rightDownGrayCnt = 0;
        int downGrayCnt = 0;
        int leftDownGrayCnt = 0;
        int loopX;
        int loopY;
        int gridStateTempGray;

        gridStateTempGray = this.getGridStateFlgGray(gridX, gridY);
        ownColor = tebanFlg;

        //クリックされたマスに何も置かれていないことを判定
        if(gridStateTempGray == NON_STATE){
            //反転色を設定
            switch(ownColor){
                case BLACK_STATE:
                  revColor = WHITE_STATE;
                  break;
                default:
                  revColor = BLACK_STATE;
                  break;
            }
            //左方向への反転チェック
            loopX = gridX -1;
            while(this.chkReverse(loopX, gridY, revColor)){
                leftGrayCnt += 1;
                loopX -= 1;

                if(loopX < 0){
                    leftGrayCnt = 0;
                    break;
                }
            }
            if(leftGrayCnt > 0){
                if(gridStateFlg[gridY][loopX] != ownColor){
                    leftGrayCnt = 0;
                }
            }
            //左上方向への反転チェック
            loopX = gridX -1;
            loopY = gridY -1;
            while(this.chkReverse(loopX, loopY, revColor)){
                leftTopGrayCnt += 1;
                loopX -= 1;
                loopY -= 1;

                if(loopX < 0 || loopY < 0){
                    leftTopGrayCnt = 0;
                    break;
                }
            }
            if(leftTopGrayCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    leftTopGrayCnt = 0;
                }
            }
            //上方向への反転チェック
            loopY = gridY -1;
            while(this.chkReverse(gridX, loopY, revColor)){
                topGrayCnt += 1;
                loopY -= 1;

                if(loopY < 0){
                    topGrayCnt = 0;
                    break;
                }
            }
            if(topGrayCnt > 0){
                if(gridStateFlg[loopY][gridX] != ownColor){
                    topGrayCnt = 0;
                }
            }
            //右上方向への反転チェック
            loopX = gridX + 1;
            loopY = gridY - 1;
            while(this.chkReverse(loopX, loopY, revColor)){
                rightTopGrayCnt += 1;
                loopX += 1;
                loopY -= 1;
                if(loopX >= GRID_X || loopY < 0){
                    rightTopGrayCnt = 0;
                    break;
                }
            }
            if(rightTopGrayCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    rightTopGrayCnt = 0;
                }
            }
            //右方向への反転チェック
            loopX = gridX + 1;
            while(this.chkReverse(loopX, gridY, revColor)){
                rightGrayCnt += 1;
                loopX += 1;

                if(loopX >= GRID_X){
                    rightGrayCnt = 0;
                    break;
                }
            }
            if(rightGrayCnt > 0){
                if(gridStateFlg[gridY][loopX] != ownColor){
                    rightGrayCnt = 0;
                }
            }
            //右下方向への反転チェック
            loopX = gridX + 1;
            loopY = gridY + 1;
            while(this.chkReverse(loopX, loopY, revColor)){
                rightDownGrayCnt += 1;
                loopX += 1;
                loopY += 1;

                if(loopX >= GRID_X || loopY >= GRID_Y){
                    rightDownGrayCnt = 0;
                    break;
                }
            }
            if(rightDownGrayCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    rightDownGrayCnt = 0;
                }
            }
            //下方向への反転チェック
            loopY = gridY + 1;
            while(this.chkReverse(gridX, loopY, revColor)){
                downGrayCnt += 1;
                loopY += 1;

                if(loopY >= GRID_Y){
                    downGrayCnt = 0;
                    break;
                }
            }
            if(downGrayCnt > 0){
                if(gridStateFlg[loopY][gridX] != ownColor){
                    downGrayCnt = 0;
                }
            }
            //左下方向への反転チェック
            loopX = gridX - 1;
            loopY = gridY + 1;
            while(this.chkReverse(loopX, loopY, revColor)){
                leftDownGrayCnt += 1;
                loopX -= 1;
                loopY += 1;

                if(loopX < 0 || loopY >= GRID_Y){
                    leftDownGrayCnt = 0;
                    break;
                }
            }
            if(leftDownGrayCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    leftDownGrayCnt = 0;
                }
            }
            //反転する石の総数を算出
            GrayCnt = leftGrayCnt + leftTopGrayCnt + topGrayCnt + rightTopGrayCnt + rightGrayCnt + rightDownGrayCnt + downGrayCnt + leftDownGrayCnt;
        }
        //反転対象がなければ
        if(GrayCnt < 1){
            //何もせずに終了
            //反転対象があれば
        }else{
            blnRet = this.actionGrayGrid(gridX, gridY, ownColor, leftGrayCnt, leftTopGrayCnt, topGrayCnt, rightTopGrayCnt, rightGrayCnt, rightDownGrayCnt, downGrayCnt, leftDownGrayCnt);
        }
        return GrayCnt;
    }

    //囲まれた石を反転する
    public int reverseGridStateFlg(int gridX, int gridY){
        boolean blnRet;
        int ownColor;
        int revColor;
        int revCnt = 0;
        int leftRevCnt = 0;
        int leftTopRevCnt = 0;
        int topRevCnt = 0;
        int rightTopRevCnt = 0;
        int rightRevCnt = 0;
        int rightDownRevCnt = 0;
        int downRevCnt = 0;
        int leftDownRevCnt = 0;
        int loopX;
        int loopY;
        int gridStateTemp;

        gridStateTemp = this.getGridStateFlg(gridX, gridY);
        ownColor = tebanFlg;

        //クリックされたマスに何も置かれていないことを判定
        if(gridStateTemp == NON_STATE){
            //反転色を設定
            switch(ownColor){
                case BLACK_STATE:
                  revColor = WHITE_STATE;
                  break;
                default:
                  revColor = BLACK_STATE;
                  break;
            }
            //左方向への反転チェック
            loopX = gridX -1;
            while(this.chkReverse(loopX, gridY, revColor)){
                leftRevCnt += 1;
                loopX -= 1;

                if(loopX < 0){
                    leftRevCnt = 0;
                    break;
                }
            }
            if(leftRevCnt > 0){
                if(gridStateFlg[gridY][loopX] != ownColor){
                    leftRevCnt = 0;
                }
            }
            //左上方向への反転チェック
            loopX = gridX -1;
            loopY = gridY -1;
            while(this.chkReverse(loopX, loopY, revColor)){
                leftTopRevCnt += 1;
                loopX -= 1;
                loopY -= 1;

                if(loopX < 0 || loopY < 0){
                    leftTopRevCnt = 0;
                    break;
                }
            }
            if(leftTopRevCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    leftTopRevCnt = 0;
                }
            }
            //上方向への反転チェック
            loopY = gridY -1;
            while(this.chkReverse(gridX, loopY, revColor)){
                topRevCnt += 1;
                loopY -= 1;

                if(loopY < 0){
                    topRevCnt = 0;
                    break;
                }
            }
            if(topRevCnt > 0){
                if(gridStateFlg[loopY][gridX] != ownColor){
                    topRevCnt = 0;
                }
            }
            //右上方向への反転チェック
            loopX = gridX + 1;
            loopY = gridY - 1;
            while(this.chkReverse(loopX, loopY, revColor)){
                rightTopRevCnt += 1;
                loopX += 1;
                loopY -= 1;
                if(loopX >= GRID_X || loopY < 0){
                    rightTopRevCnt = 0;
                    break;
                }
            }
            if(rightTopRevCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    rightTopRevCnt = 0;
                }
            }
            //右方向への反転チェック
            loopX = gridX + 1;
            while(this.chkReverse(loopX, gridY, revColor)){
                rightRevCnt += 1;
                loopX += 1;

                if(loopX >= GRID_X){
                    rightRevCnt = 0;
                    break;
                }
            }
            if(rightRevCnt > 0){
                if(gridStateFlg[gridY][loopX] != ownColor){
                    rightRevCnt = 0;
                }
            }
            //右下方向への反転チェック
            loopX = gridX + 1;
            loopY = gridY + 1;
            while(this.chkReverse(loopX, loopY, revColor)){
                rightDownRevCnt += 1;
                loopX += 1;
                loopY += 1;

                if(loopX >= GRID_X || loopY >= GRID_Y){
                    rightDownRevCnt = 0;
                    break;
                }
            }
            if(rightDownRevCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    rightDownRevCnt = 0;
                }
            }
            //下方向への反転チェック
            loopY = gridY + 1;
            while(this.chkReverse(gridX, loopY, revColor)){
                downRevCnt += 1;
                loopY += 1;

                if(loopY >= GRID_Y){
                    downRevCnt = 0;
                    break;
                }
            }
            if(downRevCnt > 0){
                if(gridStateFlg[loopY][gridX] != ownColor){
                    downRevCnt = 0;
                }
            }
            //左下方向への反転チェック
            loopX = gridX - 1;
            loopY = gridY + 1;
            while(this.chkReverse(loopX, loopY, revColor)){
                leftDownRevCnt += 1;
                loopX -= 1;
                loopY += 1;

                if(loopX < 0 || loopY >= GRID_Y){
                    leftDownRevCnt = 0;
                    break;
                }
            }
            if(leftDownRevCnt > 0){
                if(gridStateFlg[loopY][loopX] != ownColor){
                    leftDownRevCnt = 0;
                }
            }
            //反転する石の総数を算出
            revCnt = leftRevCnt + leftTopRevCnt + topRevCnt + rightTopRevCnt + rightRevCnt + rightDownRevCnt + downRevCnt + leftDownRevCnt;
            //反転対象がなければ
            if(revCnt < 1){
                //何もせずに終了
                //反転対象があれば
            }else{
                //囲んだ石を反転する
                blnRet = this.actionReverseGrid(gridX, gridY, ownColor, leftRevCnt, leftTopRevCnt, topRevCnt, rightTopRevCnt, rightRevCnt, rightDownRevCnt, downRevCnt, leftDownRevCnt);
                //今回のターンで置かれた石をセットする
                this.setGridStateFlg(gridX, gridY, ownColor);
                //手番フラグを更新
                switch(ownColor){
                    case BLACK_STATE:
                    tebanFlg = WHITE_STATE;
                    break;
                    default:
                    tebanFlg = BLACK_STATE;
                    break;
                }
            }
        }
        //戻り値には反転した石の総数を指定
        return revCnt;
    }

    private boolean chkReverse(int gridX, int gridY, int revColor){
        boolean blnRet;

        blnRet = false;
        if((gridX < 0 || gridX >= GRID_X || gridY < 0 || gridY >= GRID_Y) == false){//チェックする石が盤面上にあるか
            if(gridStateFlg[gridY][gridX] == revColor){//チェックする石が反転する色か
                blnRet = true;
            }
        }
        return blnRet;
    }
    //
    private boolean actionGrayGrid(int gridX,int gridY,int ownColor,int leftGrayCnt,int leftTopGrayCnt,int topGrayCnt,int rightTopGrayCnt,int rightGrayCnt,int rightDownGrayCnt,int downGrayCnt,int leftDownGrayCnt){
        boolean blnRet;

        blnRet = false;
        
        //
        if(leftGrayCnt > 0 || leftTopGrayCnt > 0 || topGrayCnt > 0 || rightTopGrayCnt > 0 || rightGrayCnt > 0 || rightDownGrayCnt > 0 || downGrayCnt > 0 || leftDownGrayCnt > 0){
            gridStateFlgGray[gridY][gridX] = GRAY_STATE;
            blnRet = true;
        }
        return blnRet;
    }
    //囲まれた石を反転する
    private boolean actionReverseGrid(int gridX, int gridY, int revColor, int leftRevCnt, int leftTopRevCnt, int topRevCnt, int rightTopRevCnt, int rightRevCnt, int rightDownRevCnt, int downRevCnt, int leftDownRevCnt){
        boolean blnRet;
        int revLoop;

        blnRet = false;

        //左方向の反転
        for(revLoop = 1; revLoop <= leftRevCnt; revLoop++){
            gridStateFlg[gridY][gridX - revLoop] = revColor;
            blnRet = true;
        }

        //左上方向の反転
        for(revLoop = 1; revLoop <= leftTopRevCnt; revLoop++){
            gridStateFlg[gridY - revLoop][gridX - revLoop] = revColor;
            blnRet = true;
        }

        //上方向の反転
        for(revLoop = 1; revLoop <= topRevCnt; revLoop++){
            gridStateFlg[gridY - revLoop][gridX] = revColor;
            blnRet = true;
        }

        //右上方向の反転
        for(revLoop = 1; revLoop <= rightTopRevCnt; revLoop++){
            gridStateFlg[gridY - revLoop][gridX + revLoop] = revColor;
            blnRet = true;
        }

        //右方向の反転
        for(revLoop = 1; revLoop <= rightRevCnt; revLoop++){
            gridStateFlg[gridY][gridX + revLoop] = revColor;
            blnRet = true;
        }

        //右下方向の反転
        for(revLoop = 1; revLoop <= rightDownRevCnt; revLoop++){
            gridStateFlg[gridY + revLoop][gridX + revLoop] = revColor;
            blnRet = true;
        }

        //下方向の反転
        for(revLoop = 1; revLoop <= downRevCnt; revLoop++){
            gridStateFlg[gridY + revLoop][gridX] = revColor;
            blnRet = true;
        }

        //左下方向ｎ反転
        for(revLoop = 1; revLoop <= leftDownRevCnt; revLoop++){
            gridStateFlg[gridY + revLoop][gridX - revLoop] = revColor;
            blnRet = true;
        }
        return blnRet;
    }
}