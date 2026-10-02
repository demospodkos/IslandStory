package com.example.islandstory;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new GameView());
    }

    class GameView extends View {
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        int level=0, lives=3, score=0, dialogue=0;
        float x=120, y=0, vy=0, camera=0, anim=0;
        boolean left,right,attack;
        long last;
        final float GRAV=1500f, SPEED=300f, JUMP=650f;
        final String[] names={"ТАНЯ","ВАНЯ","АЙБОЛИТ","ПИРАТ","БЕГЕМОТ","ПОЛИЦИЯ"};
        final String[][] dialog={
            {"МАМА","«В Африку не ходить. Особенно одним!»","ТАНЯ","«Но почему? Мы просто посмотрим…»","ВАНЯ","«А потом сразу домой.»"},
            {"ТАНЯ","«Вот и остров. Выглядит совсем не страшно.»","ВАНЯ","«Только не отставай. Тут кто-то есть.»","РАССКАЗЧИК","Доберитесь до тропы через джунгли."},
            {"ВАНЯ","«ТАНЯ! Смотри, кто там!»","ТАНЯ","«Ой… огромный бегемот!»","РАССКАЗЧИК","Бегите к доктору. Не попадитесь зверю."},
            {"АЙБОЛИТ","«Спокойно! Я помогу.»","ТАНЯ","«Доктор, мы просто заблудились…»","АЙБОЛИТ","«Тогда пора выбираться отсюда.»"},
            {"АЙБОЛИТ","«Нужна помощь. Я вызываю полицию!»","ПОЛИЦИЯ","«Всем оставаться на месте!»","РАССКАЗЧИК","Переживите погоню и доберитесь до лодки."},
            {"ПИРАТ","«Я думал, что мне всё можно…»","ПИРАТ","«Пожалуй, пора менять жизнь.»","РАССКАЗЧИК","Дети возвращаются домой. Приключение окончено."}
        };
        GameView(){super(MainActivity.this);setFocusable(true);post(this::loop);}
        void loop(){long n=System.currentTimeMillis();if(last==0)last=n;float dt=Math.min(.033f,(n-last)/1000f);last=n;anim+=dt;update(dt);invalidate();postDelayed(this::loop,16);}
        void update(float dt){
            if(level==0||dialogue>0)return;
            if(left)x-=SPEED*dt;if(right)x+=SPEED*dt;
            vy+=GRAV*dt;y+=vy*dt;
            float ground=getHeight()-150;
            if(y>ground){y=ground;vy=0;}
            if(attack)attack=false;
            x=Math.max(30,Math.min(1850,x));
            camera=Math.max(0,Math.min(1400,x-250));
            if(x>1800)nextLevel();
            if(level==2 && Math.abs(x-1000)<65 && y>getHeight()-230) hit();
            if(level==4 && Math.abs(x-(900+((anim*180)%700)))<70) hit();
        }
        void nextLevel(){score+=100;level++;x=80;y=0;vy=0;camera=0;dialogue=1;if(level>=6){level=6;}}
        void hit(){if(dialogue>0)return;lives--;x=Math.max(80,x-180);if(lives<=0){level=0;lives=3;score=0;dialogue=0;}}
        protected void onDraw(Canvas c){int w=getWidth(),h=getHeight();p.setStyle(Paint.Style.FILL);if(level==0){menu(c,w,h);return;}scene(c,w,h);if(dialogue>0)dialogue(c,w,h);}
        void menu(Canvas c,int w,int h){
            sky(c,w,h);p.setColor(Color.argb(180,10,15,25));c.drawRect(0,0,w,h,p);
            p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setColor(Color.WHITE);p.setTextSize(Math.min(52,w/10f));c.drawText("ПРИКЛЮЧЕНИЕ НА ОСТРОВЕ",w/2,h/2-90,p);
            p.setTypeface(Typeface.DEFAULT);p.setTextSize(21);c.drawText("Сюжетная приключенческая игра",w/2,h/2-48,p);
            button(c,w/2-145,h/2+10,290,66,"НАЧАТЬ ПРИКЛЮЧЕНИЕ");
            p.setTextSize(16);c.drawText("Таня • Ваня • Айболит • остров • погоня",w/2,h-45,p);p.setTextAlign(Paint.Align.LEFT);
        }
        void scene(Canvas c,int w,int h){
            if(level==1)home(c,w,h); else if(level==2||level==3||level==4||level==5)island(c,w,h); else ending(c,w,h);
            hud(c,w,h);
            if(level>=2&&level<=5)controls(c,w,h);
        }
        void sky(Canvas c,int w,int h){p.setShader(new LinearGradient(0,0,0,h,Color.rgb(90,170,220),Color.rgb(245,205,135),Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);}
        void home(Canvas c,int w,int h){
            sky(c,w,h);p.setColor(Color.rgb(220,205,175));c.drawRect(0,h-180,w,h,p);
            p.setColor(Color.rgb(150,85,55));c.drawRect(w/2-220,h-420,w/2+220,h-180,p);
            p.setColor(Color.rgb(85,55,40));c.drawPath(poly(w/2-250,h-420,w/2,h-590,w/2+250,h-420),p);
            p.setColor(Color.rgb(245,235,205));c.drawRect(w/2-55,h-290,w/2+55,h-180,p);
            drawTanya(c,w/2-120,h-185,1);drawVanya(c,w/2+75,h-185,1);
            label(c,"ДОМ • ПОСЛЕДНЕЕ ПРЕДУПРЕЖДЕНИЕ",24,82,Color.WHITE);
        }
        void island(Canvas c,int w,int h){
            sky(c,w,h);float off=camera*.35f;
            p.setColor(Color.rgb(55,125,70));for(int i=0;i<12;i++){float tx=i*180-off%180;c.drawRect(tx+70,h-330,tx+92,h-150,p);c.drawCircle(tx+80,h-350,78,p);}
            p.setColor(Color.rgb(220,185,105));c.drawRect(0,h-150,w,h,p);
            p.setColor(Color.rgb(30,110,65));for(int i=0;i<9;i++){float bx=i*260-(camera%260);c.drawOval(bx,h-205,bx+160,h-145,p);}
            float sx=x-camera;
            if(level==2){drawTanya(c,sx,h-150,0);drawVanya(c,sx+58,h-150,0);drawCrate(c,500-camera,h-185);}
            if(level==3){drawTanya(c,sx,h-150,0);drawVanya(c,sx+58,h-150,0);drawHippo(c,1000-camera,h-155);}
            if(level==4){drawAybolit(c,500-camera,h-155);drawTanya(c,sx,h-150,0);drawVanya(c,sx+58,h-150,0);}
            if(level==5){drawPolice(c,900-camera,h-155);drawTanya(c,sx,h-150,0);drawVanya(c,sx+58,h-150,0);drawBoat(c,1770-camera,h-175);}
            label(c,new String[]{"","ДОМ","ДЖУНГЛИ","БЕГЕМОТ","АЙБОЛИТ","ПОДКРЕПЛЕНИЕ"}[level],24,82,Color.WHITE);
        }
        void ending(Canvas c,int w,int h){island(c,w,h);drawPirate(c,w/2-25,h-160);p.setColor(Color.argb(210,0,0,0));c.drawRect(25,h-310,w-25,h-105,p);p.setColor(Color.WHITE);p.setTextSize(26);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextAlign(Paint.Align.CENTER);c.drawText("ПЕРЕОЦЕНКА ЦЕННОСТЕЙ",w/2,h-255,p);p.setTypeface(Typeface.DEFAULT);p.setTextSize(19);c.drawText("Иногда лучше послушать родителей.",w/2,h-215,p);c.drawText("Приключение завершено.",w/2,h-180,p);button(c,w/2-115,h-140,230,58,"ИГРАТЬ СНОВА");p.setTextAlign(Paint.Align.LEFT);}
        void hud(Canvas c,int w,int h){p.setColor(Color.argb(185,0,0,0));c.drawRect(0,0,w,58,p);p.setColor(Color.WHITE);p.setTextSize(19);c.drawText("❤ "+lives+"   ★ "+score,20,38,p);p.setTextAlign(Paint.Align.RIGHT);c.drawText("ЦЕЛЬ: "+(level<6?"ДОБРАТЬСЯ ДАЛЬШЕ":"ДОМ"),w-20,38,p);p.setTextAlign(Paint.Align.LEFT);}
        void dialogue(Canvas c,int w,int h){
            p.setColor(Color.argb(230,8,10,16));c.drawRoundRect(20,h-205,w-20,h-22,18,18,p);
            String[] d=dialog[Math.min(level-1,dialog.length-1)];String who=d[(dialogue-1)*2],txt=d[(dialogue-1)*2+1];
            p.setColor(Color.rgb(255,210,80));p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(20);c.drawText(who,45,h-165,p);
            p.setColor(Color.WHITE);p.setTypeface(Typeface.DEFAULT);p.setTextSize(22);for(int i=0;i<wrap(txt,48).size();i++)c.drawText(wrap(txt,48).get(i),45,h-125+i*30,p);
            p.setTextSize(15);p.setTextAlign(Paint.Align.RIGHT);c.drawText("Нажми, чтобы продолжить",w-45,h-45,p);p.setTextAlign(Paint.Align.LEFT);
        }
        ArrayList<String> wrap(String s,int n){ArrayList<String>a=new ArrayList<>();String cur="";for(String z:s.split(" ")){if(cur.length()+z.length()+1>n){a.add(cur);cur=z;}else cur+=(cur.isEmpty()?"":" ")+z;}if(!cur.isEmpty())a.add(cur);return a;}
        void controls(Canvas c,int w,int h){button(c,20,h-92,82,58,"◀");button(c,112,h-92,82,58,"▶");button(c,w-145,h-92,125,58,"ПРЫЖОК");}
        void button(Canvas c,float a,float b,float ww,float hh,String s){p.setColor(Color.argb(220,15,20,28));c.drawRoundRect(a,b,a+ww,b+hh,15,15,p);p.setColor(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(18);c.drawText(s,a+ww/2,b+37,p);p.setTextAlign(Paint.Align.LEFT);}
        void label(Canvas c,String s,float a,float b,int color){p.setColor(color);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(22);c.drawText(s,a,b,p);p.setTypeface(Typeface.DEFAULT);}
        Path poly(float...v){Path q=new Path();q.moveTo(v[0],v[1]);for(int i=2;i<v.length;i+=2)q.lineTo(v[i],v[i+1]);q.close();return q;}
        void head(Canvas c,float x,float y,int skin){p.setColor(Color.rgb(245,200,165));c.drawCircle(x,y,23,p);p.setColor(Color.BLACK);c.drawCircle(x-8,y-3,3,p);c.drawCircle(x+8,y-3,3,p);}
        void drawTanya(Canvas c,float x,float y,int mode){p.setColor(Color.rgb(205,45,65));c.drawRoundRect(x-20,y-75,x+20,y,10,10,p);head(c,x,y-98,0);p.setColor(Color.rgb(250,190,40));c.drawCircle(x,y-120,27,p);p.setColor(Color.WHITE);c.drawCircle(x,y-120,4,p);p.setColor(Color.rgb(30,30,35));c.drawRect(x-30,y-5,x-5,y+2,p);c.drawRect(x+5,y-5,x+30,y+2,p);}
        void drawVanya(Canvas c,float x,float y,int mode){p.setColor(Color.rgb(45,90,180));c.drawRoundRect(x-20,y-75,x+20,y,9,9,p);head(c,x,y-98,0);p.setColor(Color.rgb(55,90,155));c.drawOval(x-30,y-125,x+30,y-100,p);p.setColor(Color.WHITE);c.drawRect(x-20,y-45,x+20,y-25,p);p.setColor(Color.rgb(30,30,35));c.drawRect(x-30,y-5,x-5,y+2,p);c.drawRect(x+5,y-5,x+30,y+2,p);}
        void drawAybolit(Canvas c,float x,float y){p.setColor(Color.WHITE);c.drawRoundRect(x-28,y-100,x+28,y,8,8,p);head(c,x,y-125,0);p.setColor(Color.WHITE);c.drawRect(x-34,y-160,x+34,y-138,p);p.setColor(Color.RED);c.drawRect(x-6,y-157,x+6,y-141,p);c.drawRect(x-18,y-151,x+18,y-147,p);}
        void drawPirate(Canvas c,float x,float y){p.setColor(Color.rgb(45,55,75));c.drawRoundRect(x-25,y-90,x+25,y,9,9,p);head(c,x,y-115,0);p.setColor(Color.rgb(150,75,35));c.drawCircle(x,y-103,20,p);p.setColor(Color.BLACK);c.drawOval(x-38,y-150,x+38,y-132,p);}
        void drawHippo(Canvas c,float x,float y){p.setColor(Color.rgb(125,115,125));c.drawOval(x-105,y-90,x+105,y,p);c.drawCircle(x-72,y-95,34,p);c.drawCircle(x+72,y-95,34,p);p.setColor(Color.DKGRAY);c.drawCircle(x-82,y-103,6,p);c.drawCircle(x+82,y-103,6,p);p.setColor(Color.WHITE);c.drawCircle(x-20,y-20,13,p);c.drawCircle(x+20,y-20,13,p);}
        void drawPolice(Canvas c,float x,float y){for(int i=0;i<2;i++){float z=x+i*70;p.setColor(Color.rgb(35,55,100));c.drawRoundRect(z-22,y-90,z+22,y,8,8,p);head(c,z,y-115,0);p.setColor(Color.rgb(35,65,140));c.drawOval(z-28,y-145,z+28,y-125,p);}}
        void drawBoat(Canvas c,float x,float y){p.setColor(Color.rgb(125,65,40));c.drawPath(poly(x-85,y,x+85,y,x+55,y+35,x-55,y+35),p);p.setColor(Color.WHITE);c.drawRect(x-5,y-85,x+3,y,p);c.drawPath(poly(x,y-82,x+55,y-20,x,y-20),p);}
        void drawCrate(Canvas c,float x,float y){p.setColor(Color.rgb(150,95,45));c.drawRect(x,y,x+65,y+65,p);p.setColor(Color.rgb(105,60,30));c.drawRect(x+27,y,x+35,y+65,p);c.drawRect(x,y+27,x+65,y+35,p);}
        public boolean onTouchEvent(MotionEvent e){
            float tx=e.getX(),ty=e.getY();int a=e.getActionMasked();
            if(level==0){if(a==MotionEvent.ACTION_UP){level=1;dialogue=1;}return true;}
            if(level==6){if(a==MotionEvent.ACTION_UP&&ty>getHeight()-180){level=0;score=0;lives=3;dialogue=0;}return true;}
            if(dialogue>0){if(a==MotionEvent.ACTION_UP){dialogue++;if(dialogue>dialog[level-1].length/2){dialogue=0;}}return true;}
            if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_MOVE){
                left=tx<105&&ty>getHeight()-130;right=tx>=105&&tx<215&&ty>getHeight()-130;
                if(tx>getWidth()-180&&ty>getHeight()-150&&y>=getHeight()-150){vy=-JUMP;}
            }else if(a==MotionEvent.ACTION_UP){left=right=false;}
            return true;
        }
    }
}