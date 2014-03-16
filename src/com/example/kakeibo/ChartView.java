package com.example.kakeibo;

import org.afree.chart.AFreeChart;
import org.afree.chart.plot.PiePlot;
import org.afree.graphics.geom.Font;
import org.afree.graphics.geom.RectShape;
import org.afree.ui.RectangleInsets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.googlecode.androidannotations.annotations.EView;

@EView
public class ChartView extends View{
	
	private AFreeChart chart = null;
	
	public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);	
    }
	
	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);        
        if(chart == null) return;
        RectShape chartArea = new RectShape(0.0, 0.0, this.getWidth(), this.getHeight());
        this.chart.draw(canvas, chartArea);
    }

    public void setChart(AFreeChart chart) {
        this.chart = chart;
        //chart.setPadding(new RectangleInsets(10, 10, 10, 10));	//ó]îí(ãlÇﬂï®)è„Ç…10, ç∂Ç…10
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Typeface.NORMAL, 36));
    }

}