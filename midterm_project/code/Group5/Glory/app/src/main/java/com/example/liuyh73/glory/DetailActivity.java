package com.example.liuyh73.glory;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

public class DetailActivity extends AppCompatActivity {

    private Hero hero;

    private TextView outfitSuggestion1;
    private TextView outfitSuggestion2;

    private HorizontalScrollView horizontalScrollView;
    private LinearLayout outfitSuggestionList;
    private LinearLayout skillIntroduce;
    private LinearLayout outfitSuggestion;
    private TextView detailSkillIntroduce;
    private TextView detailSkillName;
    private TextView detailSkillRecoveryTime;
    private TextView detailSkillSpend;
    private TextView detailSkillTips;
    private TextView detailEquipIntroduce;
    private RelativeLayout detailSkill;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        outfitSuggestion1 = findViewById(R.id.outfitSuggestion1);
        outfitSuggestion2 = findViewById(R.id.outfitSuggestion2);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        outfitSuggestionList = findViewById(R.id.detailOutfitSuggestionList);
        detailSkillIntroduce = findViewById(R.id.detailSkillIntroduce);
        detailEquipIntroduce = findViewById(R.id.detailEquipIntroduce);
        detailSkill = findViewById(R.id.detailSkill);
        detailSkillName = findViewById(R.id.detailSkillName);
        detailSkillRecoveryTime = findViewById(R.id.detailSkillRecoveryTime);
        detailSkillSpend = findViewById(R.id.detailSkillSpend);
        detailSkillTips = findViewById(R.id.detailSkillTips);
        skillIntroduce = findViewById(R.id.detailSkillIntroduceLinearLayout);
        outfitSuggestion = findViewById(R.id.detailOutfitSuggestionLinearLayout);
        showSkills(skillIntroduce);

        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);

        hero = MainActivity.getClickedHero();

        CircleImageView skillImage1 = findViewById(R.id.skillImage1);
        CircleImageView skillImage2 = findViewById(R.id.skillImage2);
        CircleImageView skillImage3 = findViewById(R.id.skillImage3);
        CircleImageView skillImage4 = findViewById(R.id.skillImage4);
        bindSkillAndImage(skillImage1, hero.getSkills().get(0), skillImage2, skillImage3, skillImage4);
        bindSkillAndImage(skillImage2, hero.getSkills().get(1), skillImage1, skillImage3, skillImage4);
        bindSkillAndImage(skillImage3, hero.getSkills().get(2), skillImage1, skillImage2, skillImage4);
        bindSkillAndImage(skillImage4, hero.getSkills().get(3), skillImage1, skillImage2, skillImage3);

        bindEquip(hero.getHeroEquips().get(0));

        TextView skillIntroduceText = findViewById(R.id.detailAbilitiesSkillIntroduceText);
        TextView outfitSuggestionText = findViewById(R.id.detailOutfitSuggestionText);
        Drawable drawable1 = getResources().getDrawable(R.mipmap.book);
        drawable1.setBounds(0,0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        skillIntroduceText.setCompoundDrawables(drawable1, null, null, null);
        Drawable drawable2 = getResources().getDrawable(R.mipmap.head);
        drawable2.setBounds(0,0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        outfitSuggestionText.setCompoundDrawables(drawable2, null, null, null);

        ViewPager pager = findViewById(R.id.detailViewPage);
        DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager(), hero.getSkins(),(ScrollView) findViewById(R.id.scrollView));
        pager.setAdapter(adapter);
        PagerSlidingTabStrip tabs = findViewById(R.id.detailPagerSlidingTabStrip);
        tabs.setViewPager(pager);

        Skill firstSkill = hero.getSkills().get(0);
        detailSkillIntroduce.setText(firstSkill.getDescription());
        detailSkillName.setText(firstSkill.getName());
        detailSkillRecoveryTime.setText(new StringBuilder("冷却值: ").append(firstSkill.getCool()));
        detailSkillSpend.setText(new StringBuilder("消耗: ").append(firstSkill.getWaste()));
        detailSkillTips.setText(firstSkill.getTips());

        TextView heroName = findViewById(R.id.detailHeroName);
        heroName.setText(hero.getName());
        TextView positioning = findViewById(R.id.detailPositioning);
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
    }

    private void bindSkillAndImage(final ImageView imageView, final Skill skill, final ImageView imageView2, final ImageView imageView3, final ImageView imageView4) {
        Glide.with(this).load(skill.getEffectUrl()).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setBackgroundResource(R.mipmap.item2);
                imageView2.setBackgroundResource(R.mipmap.item1);
                imageView3.setBackgroundResource(R.mipmap.item1);
                imageView4.setBackgroundResource(R.mipmap.item1);
                detailSkillIntroduce.setText(skill.getDescription());
                detailSkillName.setText(skill.getName());
                detailSkillRecoveryTime.setText(new StringBuilder("冷却值: ").append(skill.getCool()));
                detailSkillSpend.setText(new StringBuilder("消耗: ").append(skill.getWaste()));
                detailSkillTips.setText(skill.getTips());
            }
        });
    }

    private void bindEquip(HeroEquip equip) {
        CircleImageView equip1 = findViewById(R.id.equip1);
        CircleImageView equip2 = findViewById(R.id.equip2);
        CircleImageView equip3 = findViewById(R.id.equip3);
        CircleImageView equip4 = findViewById(R.id.equip4);
        CircleImageView equip5 = findViewById(R.id.equip5);
        CircleImageView equip6 = findViewById(R.id.equip6);
        Glide.with(this).load(equip.getEquip(1).getImg_url()).into(equip1);
        Glide.with(this).load(equip.getEquip(2).getImg_url()).into(equip2);
        Glide.with(this).load(equip.getEquip(3).getImg_url()).into(equip3);
        Glide.with(this).load(equip.getEquip(4).getImg_url()).into(equip4);
        Glide.with(this).load(equip.getEquip(5).getImg_url()).into(equip5);
        Glide.with(this).load(equip.getEquip(6).getImg_url()).into(equip6);
        detailEquipIntroduce.setText(new StringBuilder("Tips: ").append(equip.getDescription()));
    }

    public void showSkills(View view) {
        horizontalScrollView.setVisibility(View.VISIBLE);
        outfitSuggestionList.setVisibility(View.GONE);
        skillIntroduce.setBackground(getDrawable(R.drawable.border_line));
        outfitSuggestion.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        detailSkill.setVisibility(View.VISIBLE);
        detailEquipIntroduce.setVisibility(View.GONE);
    }

    public void showOutfit(View view) {
        horizontalScrollView.setVisibility(View.GONE);
        outfitSuggestionList.setVisibility(View.VISIBLE);
        skillIntroduce.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        outfitSuggestion.setBackground(getDrawable(R.drawable.border_line));
        detailEquipIntroduce.setVisibility(View.VISIBLE);
        detailSkill.setVisibility(View.GONE);
    }

    public void showOutfit1(View view) {
        outfitSuggestion1.setBackground(getDrawable(R.drawable.border_line));
        outfitSuggestion2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        bindEquip(hero.getHeroEquips().get(0));
    }

    public void showOutfit2(View view) {
        outfitSuggestion2.setBackground(getDrawable(R.drawable.border_line));
        outfitSuggestion1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        bindEquip(hero.getHeroEquips().get(1));
    }
}
