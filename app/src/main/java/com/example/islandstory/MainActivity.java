package com.example.islandstory;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;

public class MainActivity extends Activity {
    VideoView video;
    TextView title, dialog, objective;
    Button start, left, right, jump, next;
    int chapter = 0;
    float progress = 0.18f;
    boolean playing = false;

    final String[] names = {"ДОРОГА НА ОСТРОВ","ДЖУНГЛИ","ВСТРЕЧА","АЙБОЛИТ","ПОДКРЕПЛЕНИЕ","ФИНАЛ"};
    final String[] story = {
        "Таня и Ваня слышат предупреждение родителей: в Африку не ходить.",
        "Но дети всё-таки отправляются дальше. Впереди джунгли.",
        "Из кустов появляется огромный бегемот. Нужно выбраться к тропе.",
        "Айболит замечает детей и пытается помочь им выбраться.",
        "На помощь прибывает полиция. Нужно добраться до лодки.",
        "Таня и Ваня возвращаются домой. Приключение окончено."
    };

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        build();
    }

    TextView text(String s, int size) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size);
        t.setGravity(Gravity.CENTER);
        return t;
    }

    Button button(String s) {
        Button b = new Button(this);
        b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(16);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.argb(210, 10, 15, 22)); bg.setCornerRadius(18);
        b.setBackground(bg);
        return b;
    }

    void build() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        video = new VideoView(this);
        root.addView(video, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setPadding(22, 12, 22, 18);

        title = text("ISLAND STORY", 28);
        title.setGravity(Gravity.CENTER);
        overlay.addView(title, new LinearLayout.LayoutParams(-1, 55));

        objective = text("", 16);
        objective.setGravity(Gravity.LEFT);
        overlay.addView(objective, new LinearLayout.LayoutParams(-1, 45));

        Space spacer = new Space(this);
        overlay.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1));

        dialog = text("", 20);
        dialog.setPadding(20, 10, 20, 10);
        GradientDrawable box = new GradientDrawable();
        box.setColor(Color.argb(185, 0, 0, 0)); box.setCornerRadius(20);
        dialog.setBackground(box);
        overlay.addView(dialog, new LinearLayout.LayoutParams(-1, 105));

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER);
        controls.setPadding(0, 8, 0, 0);

        left = button("◀");
        right = button("▶");
        jump = button("▲");
        next = button("ДАЛЬШЕ");

        controls.addView(left, new LinearLayout.LayoutParams(100, 65));
        controls.addView(right, new LinearLayout.LayoutParams(100, 65));
        controls.addView(jump, new LinearLayout.LayoutParams(100, 65));
        controls.addView(next, new LinearLayout.LayoutParams(180, 65));
        overlay.addView(controls);

        FrameLayout.LayoutParams op = new FrameLayout.LayoutParams(-1, -1);
        root.addView(overlay, op);

        start = button("НАЧАТЬ ПРИКЛЮЧЕНИЕ");
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(390, 80, Gravity.CENTER);
        root.addView(start, sp);

        setContentView(root);
        showMenu();

        start.setOnClickListener(v -> startGame());
        left.setOnClickListener(v -> move(-0.04f));
        right.setOnClickListener(v -> move(0.04f));
        jump.setOnClickListener(v -> {
            dialog.setText("Прыжок! Продолжай путь.");
            move(0.06f);
        });
        next.setOnClickListener(v -> nextChapter());

        video.setOnPreparedListener(mp -> {
            if (playing) {
                int pos = Math.min(chapter * 24000, Math.max(0, mp.getDuration() - 1000));
                video.seekTo(pos);
                video.start();
            }
        });
        video.setOnCompletionListener(mp -> {
            if (playing && chapter < 5) nextChapter();
        });
    }

    void showMenu() {
        playing = false;
        title.setText("ПРИКЛЮЧЕНИЕ НА ОСТРОВЕ");
        dialog.setText("Сюжетная игра по твоему ролику\nТаня • Ваня • Айболит • бегемот • полиция");
        objective.setText("Нажми «НАЧАТЬ ПРИКЛЮЧЕНИЕ»");
        start.setVisibility(Button.VISIBLE);
        left.setVisibility(Button.GONE); right.setVisibility(Button.GONE);
        jump.setVisibility(Button.GONE); next.setVisibility(Button.GONE);
        video.setVisibility(VideoView.VISIBLE);
    }

    void startGame() {
        playing = true; chapter = 0; progress = 0.18f;
        start.setVisibility(Button.GONE);
        left.setVisibility(Button.VISIBLE); right.setVisibility(Button.VISIBLE);
        jump.setVisibility(Button.VISIBLE); next.setVisibility(Button.VISIBLE);
        loadVideo();
        showChapter();
    }

    void loadVideo() {
        try {
            video.setVideoURI(Uri.parse("file:///android_asset/story.mp4"));
            video.requestFocus();
        } catch (Exception e) {
            dialog.setText("Не удалось открыть видео. Игра продолжит работать без него.");
        }
    }

    void showChapter() {
        title.setText(names[chapter]);
        dialog.setText(story[chapter]);
        objective.setText(chapter < 5
            ? "Цель: двигайся вперёд — " + Math.round(progress * 100) + "%"
            : "Цель: вернуться домой");
        if (video.getDuration() > 0) {
            video.seekTo(Math.min(chapter * 24000, Math.max(0, video.getDuration() - 1000)));
            video.start();
        }
    }

    void move(float d) {
        if (!playing) return;
        progress = Math.max(0.05f, Math.min(0.96f, progress + d));
        objective.setText("Цель: двигайся вперёд — " + Math.round(progress * 100) + "%");
        if (progress >= 0.96f && chapter < 5) nextChapter();
    }

    void nextChapter() {
        if (!playing) return;
        if (chapter < 5) {
            chapter++;
            progress = 0.12f;
            showChapter();
        } else {
            playing = false;
            if (video.isPlaying()) video.pause();
            title.setText("КОНЕЦ");
            dialog.setText("Таня и Ваня вернулись домой. Спасибо за прохождение!");
            objective.setText("Нажми «НАЧАТЬ ПРИКЛЮЧЕНИЕ», чтобы сыграть снова.");
            next.setVisibility(Button.GONE);
            start.setText("ИГРАТЬ СНОВА");
            start.setVisibility(Button.VISIBLE);
        }
    }
}
