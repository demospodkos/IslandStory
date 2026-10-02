package com.example.islandstory;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;

public class MainActivity extends Activity {
    StoryView game;
    @Override public void onCreate(Bundle b){ super.onCreate(b); getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); game=new StoryView(); setContentView(game); }

    class StoryView extends View {
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); int scene=0,lives=3,stars=0; float px=100,py=0,vy=0; boolean left,right; long last=0; boolean jump;
        final String[] titles={"НАЧАЛО ИСТОРИИ","ГЛАВА 1 · ДОМ","ГЛАВА 2 · ОСТРОВ","ГЛАВА 3 · ОПАСНАЯ ВСТРЕЧА","ГЛАВА 4 · АЙБОЛИТ","ГЛАВА 5 · ПОДКРЕПЛЕНИЕ","ФИНАЛ · ПЕРЕОЦЕНКА ЦЕННОСТЕЙ"};
        final String[] texts={
            "По мотивам загруженного ролика. Личная фанатская игра.\nНажми ПЕРВЫЙ ШАГ, чтобы начать.",
            "Родители строго предупреждают детей: в Африку ходить нельзя. Но Таня и Ваня решают проверить, что же там на самом деле.",
            "На острове всё совсем не так, как дома. Пальмы, джунгли и неизвестный путь. Доберись до отметки и не попадайся опасностям.",
            "Детей ждёт встреча с огромным животным и человеком, который слишком уверенно чувствует себя хозяином острова.",
            "Айболит появляется как отзывчивый врач и пытается разобраться с последствиями приключения.",
            "Айболит вызывает подкрепление полиции. Нужно пережить последнюю опасную сцену и добраться до безопасного места.",
            "У бородатого героя случается полная переоценка ценностей. История возвращается домой — но приключение уже не забыть."
        };
        StoryView(){ super(MainActivity.this); setFocusable(true); post(loop); }
        void loop(){long now=System.currentTimeMillis();if(last==0)last=now;float dt=Math.min(.04f,(now-last)/1000f);last=now;update(dt);invalidate();postDelayed(this::loop,16);}
        void update(float dt){if(scene<2||scene>5)return;if(left)px-=260*dt;if(right)px+=260*dt;vy+=950*dt;py+=vy*dt;float ground=getHeight()-150;if(py>ground){py=ground;vy=0;}if(jump&&py>=ground-1){vy=-520;jump=false;}if(px>getWidth()-90){px=70;scene++;if(scene>6)scene=6;}if(px<20)px=20;}
        protected void onDraw(Canvas c){super.onDraw(c);int w=getWidth(),h=getHeight();if(scene==0){drawIntro(c,w,h);return;}drawScene(c,w,h);}
        void bg(Canvas c,int w,int h,int mode){p.setShader(new LinearGradient(0,0,0,h,mode==1?Color.rgb(70,145,185):Color.rgb(90,190,220),mode==1?Color.rgb(220,180,120):Color.rgb(150,205,135),Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);if(mode>1){p.setColor(Color.rgb(38,105,57));for(int i=0;i<10;i++){float x=i*130-(px%130);c.drawCircle(x+45,150,70,p);c.drawRect(x+40,145,x+55,h-130,p);}}}
        void drawIntro(Canvas c,int w,int h){bg(c,w,h,1);p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(Math.min(58,w/10f));c.drawText("ПРИКЛЮЧЕНИЕ НА ОСТРОВЕ",w/2,h/2-70,p);p.setTypeface(Typeface.DEFAULT);p.setTextSize(22);c.drawText("Личная фанатская игра по сюжету загруженного видео",w/2,h/2-25,p);button(c,w/2-130,h/2+40,260,64,"ПЕРВЫЙ ШАГ");p.setTextAlign(Paint.Align.LEFT);}
        void drawScene(Canvas c,int w,int h){bg(c,w,h,scene>=2?2:1);p.setColor(Color.argb(190,0,0,0));c.drawRect(0,0,w,72,p);p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(25);c.drawText(titles[Math.min(scene,6)],22,45,p);p.setTypeface(Typeface.DEFAULT);c.drawText("♥ "+lives+"   ★ "+stars,w-170,45,p);
            if(scene>=2&&scene<=5){p.setColor(Color.rgb(211,174,108));c.drawRect(0,h-120,w,h,p);drawCharacter(c,px,h-150,false);drawThreats(c,w,h);controls(c,w,h);}else{drawCharacter(c,w*.22f,h-210,scene==6);drawStoryCard(c,w,h,texts[scene]);button(c,w-250,h-95,210,60,scene==6?"ЗАНОВО":"ПРОДОЛЖИТЬ");}}
        void drawCharacter(Canvas c,float x,float y,boolean beard){p.setColor(Color.rgb(50,50,60));c.drawRoundRect(x,y-50,x+45,y,12,12,p);p.setColor(Color.rgb(244,200,165));c.drawCircle(x+22,y-65,22,p);p.setColor(Color.rgb(105,65,35));c.drawRect(x+2,y-88,x+42,y-80,p);if(beard){p.setColor(Color.rgb(165,85,45));c.drawCircle(x+22,y-55,20,p);}p.setColor(Color.WHITE);c.drawCircle(x+14,y-68,4,p);c.drawCircle(x+30,y-68,4,p);}
        void drawThreats(Canvas c,int w,int h){if(scene==2){p.setColor(Color.rgb(75,130,60));c.drawOval(w-180,h-190,w-30,h-95,p);p.setColor(Color.rgb(50,80,45));c.drawCircle(w-125,h-145,9,p);c.drawCircle(w-80,h-145,9,p);}if(scene==3){p.setColor(Color.rgb(90,90,95));c.drawOval(w-260,h-210,w-30,h-100,p);p.setColor(Color.rgb(230,230,210));c.drawCircle(w-190,h-175,28,p);c.drawCircle(w-105,h-175,28,p);}if(scene==4){p.setColor(Color.WHITE);c.drawRect(w-250,h-220,w-80,h-80,p);p.setColor(Color.RED);c.drawRect(w-180,h-205,w-150,h-115,p);c.drawRect(w-205,h-175,w-125,h-145,p);}if(scene==5){p.setColor(Color.rgb(35,45,75));c.drawRoundRect(w-250,h-220,w-165,h-80,15,15,p);c.drawRoundRect(w-135,h-220,w-50,h-80,15,15,p);}}
        void drawStoryCard(Canvas c,int w,int h,String s){p.setColor(Color.argb(215,0,0,0));c.drawRoundRect(28,h-255,w-28,h-112,18,18,p);p.setColor(Color.WHITE);p.setTextSize(22);float yy=h-220;for(String line:s.split("\\n")){for(String part:wrap(line,50)){c.drawText(part,52,yy,p);yy+=29;}}}
        java.util.List<String> wrap(String s,int n){java.util.List<String> out=new java.util.ArrayList<>();String cur="";for(String word:s.split(" ")){if((cur+" "+word).trim().length()>n){out.add(cur);cur=word;}else cur=(cur+" "+word).trim();}if(!cur.isEmpty())out.add(cur);return out;}
        void controls(Canvas c,int w,int h){button(c,20,h-95,90,60,"◀");button(c,120,h-95,90,60,"▶");button(c,w-150,h-95,130,60,"ПРЫЖОК");}
        void button(Canvas c,float x,float y,float ww,float hh,String s){p.setColor(Color.argb(225,20,25,35));c.drawRoundRect(x,y,x+ww,y+hh,16,16,p);p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(20);c.drawText(s,x+ww/2,y+hh/2+7,p);p.setTextAlign(Paint.Align.LEFT);}
        public boolean onTouchEvent(MotionEvent e){float x=e.getX(),y=e.getY();int a=e.getActionMasked();if(scene==0&&a==ACTION_UP){scene=1;return true;}if((scene==1||scene==6)&&a==ACTION_UP&&y>getHeight()-130){if(scene==6){scene=0;lives=3;stars=0;}else scene=2;return true;}if(scene>=2&&scene<=5){if(a==ACTION_DOWN||a==ACTION_MOVE){left=x<110&&y>getHeight()-150;right=x>=110&&x<230&&y>getHeight()-150;if(x>getWidth()-190&&y>getHeight()-160){jump=true;stars++;}}else if(a==ACTION_UP){left=right=false;}return true;}return true;}
    }
}
