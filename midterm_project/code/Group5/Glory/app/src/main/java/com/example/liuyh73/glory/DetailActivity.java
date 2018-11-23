package com.example.liuyh73.glory;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    private Hero hero;
    private DetailPagerAdapter adapter;
    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    private TextView heroName;
    private TextView positioning;

    TextView outfitSuggestion1;
    TextView outfitSuggestion2;

    HorizontalScrollView horizontalScrollView;
    LinearLayout outfitSuggestion;
    TextView skillIntroduceText;
    TextView outfitSuggestionText;
    TextView detailIntroduce;
    CircleImageView equip1;
    CircleImageView equip2;
    CircleImageView equip3;
    CircleImageView equip4;
    CircleImageView equip5;
    CircleImageView equip6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        outfitSuggestion1 = findViewById(R.id.outfitSuggestion1);
        outfitSuggestion2 = findViewById(R.id.outfitSuggestion2);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        outfitSuggestion = findViewById(R.id.detailOutfitSuggestion);
        skillIntroduceText = findViewById(R.id.detailAbilitiesSkillIntroduceText);
        outfitSuggestionText = findViewById(R.id.detailOutfitSuggestionText);
        detailIntroduce = findViewById(R.id.detailIntroduce);
        equip1 = findViewById(R.id.equip1);
        equip2 = findViewById(R.id.equip2);
        equip3 = findViewById(R.id.equip3);
        equip4 = findViewById(R.id.equip4);
        equip5 = findViewById(R.id.equip5);
        equip6 = findViewById(R.id.equip6);

        MyDB myDB = new MyDB(getApplicationContext());
        hero = MainActivity.getClickedHero();

        heroName = findViewById(R.id.detailHeroName);
        heroName.setText(hero.getName());
        positioning = findViewById(R.id.detailPositioning);
        positioning.setText(hero.getHero_type());

        DonutProgress donutSurviveProgress = findViewById(R.id.donutSurviveProgress);
        DonutProgress donutAttackProgress = findViewById(R.id.donutAttackProgress);
        DonutProgress donutSkillProgress = findViewById(R.id.donutSkillProgress);
        DonutProgress donutDifficultProgress = findViewById(R.id.donutDifficultProgress);

        donutSurviveProgress.setProgress(hero.getLive());
        donutSurviveProgress.setText(String.valueOf(hero.getLive() / 10));
        donutAttackProgress.setProgress(hero.getAttack());
        donutAttackProgress.setText(String.valueOf(hero.getAttack() / 10));
        donutSkillProgress.setProgress(hero.getSkill());
        donutSkillProgress.setText(String.valueOf(hero.getSkill() / 10));
        donutDifficultProgress.setProgress(hero.getDifficulty());
        donutDifficultProgress.setText(String.valueOf(hero.getDifficulty() / 10));

        pager = findViewById(R.id.detailViewPage);
        adapter = new DetailPagerAdapter(getSupportFragmentManager(), hero.getSkins());
        pager.setAdapter(adapter);
        tabs = findViewById(R.id.detailPagerSlidingTabStrip);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(0);

        CircleImageView skillImage1 = findViewById(R.id.skillImage1);
        CircleImageView skillImage2 = findViewById(R.id.skillImage2);
        CircleImageView skillImage3 = findViewById(R.id.skillImage3);
        CircleImageView skillImage4 = findViewById(R.id.skillImage4);
        bindSkillAndImage(skillImage1, hero.getSkills().get(0), skillImage2, skillImage3, skillImage4);
        bindSkillAndImage(skillImage2, hero.getSkills().get(1), skillImage1, skillImage3, skillImage4);
        bindSkillAndImage(skillImage3, hero.getSkills().get(2), skillImage1, skillImage2, skillImage4);
        bindSkillAndImage(skillImage4, hero.getSkills().get(3), skillImage1, skillImage2, skillImage3);
    }

    private void bindSkillAndImage(final ImageView imageView, final Skill skill, final ImageView imageView2, final ImageView imageView3, final ImageView imageView4) {
        DownloadImage.setViewImage(imageView, skill.getEffectUrl());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setBackgroundResource(R.mipmap.item2);
                imageView2.setBackgroundResource(R.mipmap.item1);
                imageView3.setBackgroundResource(R.mipmap.item1);
                imageView4.setBackgroundResource(R.mipmap.item1);
                detailIntroduce.setText(skill.getDescription());
            }
        });
    }

    private void bindEquip(HeroEquip equip) {
        DownloadImage.setViewImage(equip1, equip.getEquip(0).getImg_url());
        DownloadImage.setViewImage(equip2, equip.getEquip(1).getImg_url());
        DownloadImage.setViewImage(equip3, equip.getEquip(2).getImg_url());
        DownloadImage.setViewImage(equip4, equip.getEquip(3).getImg_url());
        DownloadImage.setViewImage(equip5, equip.getEquip(4).getImg_url());
        DownloadImage.setViewImage(equip6, equip.getEquip(5).getImg_url());
        detailIntroduce.setText(equip.getDescription());
    }

    public void showSkills(View view) {
        horizontalScrollView.setVisibility(View.VISIBLE);
        outfitSuggestion.setVisibility(View.GONE);
        skillIntroduceText.setBackground(getDrawable(R.drawable.border_line));
        outfitSuggestionText.setBackgroundResource(0);
    }


    public void showOutfit(View view) {
        horizontalScrollView.setVisibility(View.GONE);
        outfitSuggestion.setVisibility(View.VISIBLE);
        skillIntroduceText.setBackgroundResource(0);
        outfitSuggestionText.setBackground(getDrawable(R.drawable.border_line));
    }

    public void showOutfit1(View view) {
        outfitSuggestion1.setBackground(getDrawable(R.drawable.border_line));
        outfitSuggestion2.setBackgroundResource(0);
    }

    public void showOutfit2(View view) {
        outfitSuggestion2.setBackground(getDrawable(R.drawable.border_line));
        outfitSuggestion1.setBackgroundResource(0);
    }
}
