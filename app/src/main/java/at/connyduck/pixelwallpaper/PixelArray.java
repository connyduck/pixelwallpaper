package at.connyduck.pixelwallpaper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

public class PixelArray {

	private int pixelsize;

	private boolean border;
    private boolean blackWhiteMode;

	private ArrayList<Rect> rects;
	private ArrayList<IntList> colors;

	private int maxPixelsPerRow;  //max pixels - for calculation
	private int maxPixelsPerColumn;
	private int pixelsPerRow;  //current Pixels - for drawing
	private int pixelsPerColumn;

	private Random random;

	private Paint paint;

	private int redTouchConstant;
	private int greenTouchConstant;
	private int blueTouchConstant;

	private int greenC;
	private int blueC;
	private int redC;

	private int brightD;

	private int colorCount;
	private int nextColor;
	private int colorDirection;

	private int brightnessCount;
	private int nextBrightness;

	private PersistenceManager pm;

	public PixelArray(int touchmode, boolean blackWhiteMode) {

		setTouchmode(touchmode);

        this.blackWhiteMode = blackWhiteMode;

		this.random = new Random();

		this.paint = new Paint();

		this.redC = 0;
		this.greenC = 0;
		this.blueC = 0;

		this.brightD = 0;

		this.colorCount = 0;
		this.nextColor = random.nextInt(6);
		if(random.nextBoolean()) colorDirection = 1;
		else colorDirection = -1;

		this.brightnessCount = 0;
		this.nextBrightness = random.nextInt(4);

	}

	public void setTouchmode(int touchmode) {
		if(touchmode == 1) { //white
			redTouchConstant = 25;
			greenTouchConstant = 25;
			blueTouchConstant = 25;
		}
		if(touchmode == 2) { //black
			redTouchConstant = -25;
			greenTouchConstant = -25;
			blueTouchConstant = -25;
		}
		if(touchmode == 3) { //red
			redTouchConstant = 40;
			greenTouchConstant = -10;
			blueTouchConstant = -10;
		}
		if(touchmode == 4) { //green
			redTouchConstant = -10;
			greenTouchConstant = 40;
			blueTouchConstant = -10;
		}
		if(touchmode == 5) { //blue
			redTouchConstant = -10;
			greenTouchConstant = -10;
			blueTouchConstant = 40;
		}
	}

	public void setBlackWhiteMode(boolean blackWhiteMode) {
        this.blackWhiteMode = blackWhiteMode;
    }

	public void reset(int width, int height, int pixelsize, boolean border) {

		this.pixelsize = pixelsize;

		this.border = border;

		this.pixelsPerRow = width/pixelsize;
		if(width%pixelsize!=0) pixelsPerRow++;
		this.pixelsPerColumn = height/pixelsize;
		if(height%pixelsize!=0) pixelsPerColumn++;

		this.maxPixelsPerRow = pixelsPerRow;
		this.maxPixelsPerColumn = pixelsPerColumn;

		createRects(border);
		createPixelColors();

    }

	public void reinitialize(int width, int height) {

		pixelsPerRow = width/pixelsize;
		if(width%pixelsize!=0) pixelsPerRow++;
		pixelsPerColumn = height/pixelsize;
		if(height%pixelsize!=0) pixelsPerColumn++;

		if(pixelsPerRow>maxPixelsPerRow || pixelsPerColumn>maxPixelsPerColumn) {
			resizeColorArray(pixelsPerRow, pixelsPerColumn);
			createRects(border);
		}

	}

	public void initialize(Context c, int width, int height, int pixelsize, boolean border, boolean isPreview) {

		this.pm = new PersistenceManager(c);

		this.pixelsize = pixelsize;

		this.border = border;

		int ppr = width/pixelsize;
		if(width%pixelsize!=0) ppr++;
		int ppc = height/pixelsize;
		if(height%pixelsize!=0) ppc++;

		this.pixelsPerRow = ppr;
		this.pixelsPerColumn = ppc;

		if( !isPreview && pm.load()) {
			this.maxPixelsPerRow = pm.getMaxPixelsPerRow();
			this.maxPixelsPerColumn = pm.getMaxPixelsPerColumn();
			this.colors = pm.getColors();
			this.colorCount = pm.getColorCount();
			this.nextColor = pm.getNextColor();
			this.colorDirection = pm.getColorDirection();
			this.brightnessCount = pm.getBrightnessCount();
			this.nextBrightness = pm.getNextBrightness();

		} else {
			this.maxPixelsPerRow = ppr;
			this.maxPixelsPerColumn = ppc;
			createPixelColors();
		}

		setColors();
		setBrightness();

		if(ppr>maxPixelsPerRow || ppc>maxPixelsPerColumn) {
			resizeColorArray(ppr, ppc);
		}

		createRects(border);

	}

	private void createRects(boolean border) {
		this.rects = new ArrayList<>();

		int x;
		int y=0;

		int margin = 0;
		if(border) margin = -1;

		while(y<maxPixelsPerColumn) {
			x=0;
			while(x<maxPixelsPerRow) {

				Rect r = new Rect(x*pixelsize, y*pixelsize, x*pixelsize+pixelsize+margin, y*pixelsize+pixelsize+margin);
				rects.add(r);

				x++;
			}
			y++;

		}
	}

	private void createPixelColors() {
		this.colors = new ArrayList<>(maxPixelsPerColumn);

		int x;
		int y=0;

		while(y<maxPixelsPerColumn) {
			x=0;
			IntList row = new IntList(maxPixelsPerRow);
			colors.add(row);
			while(x<maxPixelsPerRow) {
				int p = createNewPixelColor(x, y);
				row.add(p);
				x++;
			}

			y++;

		}

	}

	private int createNewPixelColor(int x, int y) {

		int col, b, c;
		if(x!=0) {
			b = getPixelColorAt(x - 1, y);
			if(y!=0) {
				c = getPixelColorAt(x, y - 1);
				col = getNewColor(b, c);
			} else {
				col = b;
			}
		} else {
			if(y!=0) {
				col = getPixelColorAt(x, y - 1);

			} else {
				col= Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
			}

		}

		int red = Color.red(col)-12+random.nextInt(25);
		int green = Color.green(col)-12+random.nextInt(25);
		int blue = Color.blue(col)-12+random.nextInt(25);

		if(red>255) red = 255;
		if(red<0) red = 0;
		if(green>255) green = 255;
		if(green<0) green = 0;
		if(blue>255) blue = 255;
		if(blue<0) blue = 0;

		return Color.rgb(red, green, blue);
	}

	private void resizeColorArray(int ppr, int ppc) {

		for(int y=0; y<maxPixelsPerColumn; y++) {
			for(int x=maxPixelsPerRow; x<ppr; x++) {

				int p = createNewPixelColor(x, y);
				colors.get(y).add(p);
			}
		}
		if(maxPixelsPerRow<ppr) maxPixelsPerRow = ppr;

		for(int y=maxPixelsPerColumn; y<ppc; y++) {

			IntList row = new IntList(maxPixelsPerRow);
			colors.add(row);

			for(int x=0; x<maxPixelsPerRow; x++) {
				int p = createNewPixelColor(x, y);
				row.add(p);
			}
		}


		if(maxPixelsPerColumn<ppc) maxPixelsPerColumn = ppc;

    }

	public void draw(Canvas c) {

		c.drawColor(Color.BLACK);

		for(int j=0; j<pixelsPerColumn; j++) {
			for(int i=0; i<pixelsPerRow; i++) {
				paint.setColor(getPixelColorAt(i, j));
				c.drawRect(getRectAt(i,j), paint);
			}

		}

	}

	public void touch(double x1, double y1) {

		int x = (int)(x1/pixelsize);
		int y = (int)(y1/pixelsize);

		if(x>=maxPixelsPerRow || y>=maxPixelsPerColumn) return;

		int p = getPixelColorAt(x, y);

		int red = Color.red(p)+redTouchConstant;
		int green = Color.green(p)+greenTouchConstant;
		int blue = Color.blue(p)+blueTouchConstant;

		if(red>255) red = 255;
		if(red<0) red = 0;
		if(green>255) green = 255;
		if(green<0) green = 0;
		if(blue>255) blue = 255;
		if(blue<0) blue = 0;

		colors.get(y).set(x, Color.rgb(red, green, blue));

	}

	private int getPixelColorAt(int x, int y) {
        int color = colors.get(y).get(x);
        if(blackWhiteMode) {
            color = (Color.red(color)+Color.green(color)+Color.blue(color))/3;
            return Color.rgb(color, color, color);
        }
		return color;
	}

	private Rect getRectAt(int x, int y) {
		return rects.get(x + y * maxPixelsPerRow);
	}

	private int getNewColor(int a, int b) {
		int red = (Color.red(a)+Color.red(b))/2;
		int green = (Color.green(a)+Color.green(b))/2;
		int blue = (Color.blue(a)+Color.blue(b))/2;
		return Color.rgb(red, green, blue);
	}

	public void update() {

        int x;
		int y=0;

		int red, green, blue;

		while(y<maxPixelsPerColumn) {
			x=0;

			while(x<maxPixelsPerRow) {

				if(random.nextInt(30)==0){
					int colD = 1;
					int a;
					int p = getPixelColorAt(x, y);

					red = Color.red(p);
					green = Color.green(p);
					blue = Color.blue(p);


					if(x!=0) {
						a = getPixelColorAt(x - 1, y);
						red = red+Color.red(a);
						green = green+Color.green(a);
						blue = blue+Color.blue(a);
						colD++;
					}
					if(y!=0) {
						a = getPixelColorAt(x, y - 1);
						red = red+Color.red(a);
						green = green+Color.green(a);
						blue = blue+Color.blue(a);
						colD++;
					}
					if(x!=maxPixelsPerRow-1) {
						a = getPixelColorAt(x + 1, y);
						red = red+Color.red(a);
						green = green+Color.green(a);
						blue = blue+Color.blue(a);
						colD++;
					}
					if(y!=maxPixelsPerColumn-1) {
						a = getPixelColorAt(x, y + 1);
						red = red+Color.red(a);
						green = green+Color.green(a);
						blue = blue+Color.blue(a);
						colD++;
					}

					red = red/colD+30-random.nextInt(61)+redC+brightD;
					green = green/colD+30-random.nextInt(61)+greenC+brightD;
					blue = blue/colD+30-random.nextInt(61)+blueC+brightD;

					if(red>255) red = 255;
					if(red<0) red = 0;
					if(green>255) green = 255;
					if(green<0) green = 0;
					if(blue>255) blue = 255;
					if(blue<0) blue = 0;

                    colors.get(y).set(x, Color.rgb(red, green, blue));
				}
				x++;

			}
			y++;

		}

		colorCount--;
		if(colorCount<0) {
			colorCount = 1150+random.nextInt(700);
			nextColor = nextColor+colorDirection;
			if(nextColor<0) nextColor = 5;
			if(nextColor>5) nextColor = 0;
			setColors();
			if(random.nextInt(10)==0) {
				if(colorDirection==1) colorDirection = -1;
				else colorDirection = 1;
			}
		}

		brightnessCount--;
		if(brightnessCount<0) {
			brightnessCount = 2000+random.nextInt(3000);
			nextBrightness = (nextBrightness+1)%4;
			setBrightness();
		}

	}

	public void save() {

		pm.setAll(colors, maxPixelsPerRow, maxPixelsPerColumn, colorCount, nextColor, colorDirection, brightnessCount, nextBrightness);

		pm.save();

	}


	private void setColors() {
		if(nextColor==0) {//blue
			redC = -3;
			greenC = -3;
			blueC = 6;
		}
		if(nextColor==1) {//cyan
			redC = -6;
			greenC = 3;
			blueC = 3;
		}
		if(nextColor==2) {//green
			redC = -3;
			greenC = 6;
			blueC = -3;
		}
		if(nextColor==3) {//yellow
			redC = 3;
			greenC = 3;
			blueC = -6;
		}
		if(nextColor==4) {//red
			redC = 6;
			greenC = -3;
			blueC = -3;
		}
		if(nextColor==5) {//magenta
			redC = 3;
			greenC = -6;
			blueC = 3;
		}
	}

	private void setBrightness(){

		if(nextBrightness==0) {
			brightD = 3;
		}
		if(nextBrightness==1) {
			brightD = 0;
		}
		if(nextBrightness==2) {
			brightD = -3;
		}
		if(nextBrightness==3) {
			brightD = 0;
		}
	}

}