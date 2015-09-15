package com.shikhar.androidgames.framework;

import android.graphics.Paint;

public interface Graphics {
	public static enum ImageFormat {
		ARGB8888, ARGB4444, RGB565
	}

	public Image newImage(String fileName, ImageFormat format);


	public void clearScreen(int color);


	public void drawPixel(int x, int y, int color);


	public void drawARGB(int a, int r, int g, int b);

	public void drawLine(int x, int y, int x2, int y2, int color);


	public void drawRect(int x, int y, int width, int height, int color);


	public void drawCircle(float centerX, float centerY, float radius, int color);

	public void drawImage(Image pixmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight);

	public void drawImage(Image pixmap, int x, int y);

	
	public void drawString(String text, int x, int y, Paint paint);


	public int getWidth();


	public int getHeight();
}
