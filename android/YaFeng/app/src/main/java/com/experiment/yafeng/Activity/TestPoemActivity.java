package com.experiment.yafeng.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.experiment.yafeng.R;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xuexiang.xui.XUI.getContext;

public class TestPoemActivity extends AppCompatActivity {

    private TitleBar titleBar;
    private TextView title;
    private TextView author;
    private Button button;
    private TextView content;
    private List<String> punctuations = new ArrayList<String>();
    private String poemTexts[];
    private String tempTexts[];
    private ConstraintLayout pageLayout;
    private List<Integer> idRecord = new ArrayList<Integer>();
    private List<EditText> editTexts = new ArrayList<EditText>();
    private boolean isRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_poem);
        initViews();

        /*获取上一页面的传输数据*/
        String titleText = getIntent().getStringExtra("title");
        String tempTitleText = titleText;
        String dynastyText = getIntent().getStringExtra("dynasty");
        String tempDynastyText = dynastyText;
        String authorText = getIntent().getStringExtra("author");
        String tempAuthorText = authorText;
        String contentText = getIntent().getStringExtra("content");
        String tempContentText = contentText;
        Random random = new Random();

        /*初始化testText--content*/
        spiltPoemText(contentText);
        int sentenceCount = random.nextInt(poemTexts.length);//随机生成填空的个数
        for (int i = 0; i < sentenceCount; i++) {
            initTestText();
        }

        tempContentText = integrateTexts();
        Log.d("Final:", tempContentText);
        pageLayout = findViewById(R.id.content);
        String[] paragraph = spiltPoemTextByEnter(tempContentText);
        int idCount = 1;


        /*初始化testPage*/
        titleBar.setTitle(getIntent().getStringExtra("title"));
        title.setText(tempTitleText);
        author.setText(tempDynastyText + "." + tempAuthorText);

        // 按换行符分段，按标点分居，段落之间使用约束布局，句子之间使用线性布局
        for (int i = 0; i < paragraph.length; i++) {
            LinearLayout paragraphL = new LinearLayout(this);
            String[] sentence = spiltPoemTextByPunctuation(paragraph[i]);
            boolean isEdit = false;
            for (int j = 0; j < sentence.length; j++) //展示该段的句子
            {
                if (sentence[j].indexOf("_") != -1) //需编辑的句子以EditText展示
                {
                    isEdit = true;
                    Log.d("edittext" + idCount + "_id=", " " + idCount);
                    EditText editText = new EditText(this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.topMargin = 5;
                    lp.leftMargin = 5;
                    lp.rightMargin = 5;
                    editText.setLayoutParams(lp);
                    editText.setLineSpacing(0, 1.3f);
                    editText.setBackground(null);
                    editText.setEnabled(true);
                    editText.setClickable(true);
                    editText.setFocusableInTouchMode(false);
                    editText.setFocusable(false);
                    editText.setText(sentence[j]);
                    editText.setTextSize(18);
                    editText.setId(idCount);
                    idRecord.add(idCount);
                    editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    editText.setLayoutParams(lp);
                    paragraphL.addView(editText);
                    editTexts.add(editText);
                } else// 不需编辑的句子以TextView展示
                {
                    Log.d("textview" + idCount + "_id=", " " + idCount);
                    TextView textView = new TextView(this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.topMargin = 5;
                    lp.leftMargin = 5;
                    lp.rightMargin = 5;
                    textView.setLayoutParams(lp);
                    textView.setLineSpacing(0, 1.3f);
                    if (idCount > 1 && j != 0 && isEdit == true) {
                        textView.setText(punctuations.get(idCount - 2) + sentence[j] + punctuations.get(idCount - 1));
                        isEdit = false;
                    } else
                        textView.setText(sentence[j] + punctuations.get(idCount - 1));
                    textView.setTextSize(18);
                    textView.setId(idCount);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setLayoutParams(lp);
                    paragraphL.addView(textView);
                }
                idCount++;//idCount为句子对应的索引
            }
            ConstraintLayout.LayoutParams cp = new ConstraintLayout.LayoutParams(ConstraintLayout.
                    LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT);
            cp.topMargin = 5;
            cp.leftMargin = 5;
            cp.rightMargin = 5;
            //cp.topMargin=10;
            paragraphL.setId(tempTexts.length + i + 1);
            if (i == 0)
                cp.topToTop = R.id.content;
            else
                cp.topToBottom = tempTexts.length + i;
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            paragraphL.setLayoutParams(cp);
            paragraphL.setOrientation(LinearLayout.HORIZONTAL);
            pageLayout.addView(paragraphL);
        }

        //对可输入部分的输入事件进行处理
        for (int i = 0; i < editTexts.size(); i++) {
            EditText temp = editTexts.get(i);
            int index = temp.getText().length();
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    temp.setText("");
                    temp.setFocusable(true);//重新获取焦点
                    temp.setFocusableInTouchMode(true);
                    temp.requestFocus();
                    temp.requestFocusFromTouch();
                    InputMethodManager inputManager = (InputMethodManager) temp.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);//弹出软键盘
                    inputManager.showSoftInput(temp, 0);
                }
            });

            temp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int index = temp.getId();
                    if (poemTexts[index - 1].indexOf(s.toString()) != -1) {
                        isRight = true;
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
                        s.setSpan(span, 0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    } else {
                        isRight = false;
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                        s.setSpan(span, 0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    UnderlineSpan underlineSpan = new UnderlineSpan();
                    s.setSpan(underlineSpan, 0, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            });
        }

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRight == false) {
                    Toast.makeText(getContext(), "大侠再回去看看吧!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), "恭喜你已经记住了！", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initViews() //初始化view
    {
        titleBar = findViewById(R.id.testTitleBar);
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        button = findViewById(R.id.check_button);
        //content=findViewById(R.id.content);
    }

    private void initTestText() //初始化测试文本
    {
        Random random = new Random();
        int index = random.nextInt(tempTexts.length);//随机选取一个句子
        String temp = "";
        temp = tempTexts[index];
        String temp1 = "";
        for (int i = 0; i < temp.length(); i++)//将该句子用_替换
        {
            temp1 += "_";
        }
        if (temp.charAt(0) == '\n')
            temp1 = "\n" + temp1;
        tempTexts[index] = temp1;
    }

    private String integrateTexts() //合并字符串
    {
        String result = "";
        for (int i = 0; i < tempTexts.length - 1; i++) {
            result += tempTexts[i] + punctuations.get(i);
        }
        return result;
    }

    private void spiltPoemText(String poemText) //按标点分割字符串
    {
        poemTexts = poemText.split("、|，|。|；|？|！");
        tempTexts = poemText.split("、|，|。|；|？|！");
        Pattern p = Pattern.compile("、|，|。|；|！|？");
        Matcher m = p.matcher(poemText);
        while (m.find()) {
            punctuations.add(m.group());
        }
    }

    private String[] spiltPoemTextByEnter(String poemText)//按换行分割字符串
    {
        String result[];
        result = poemText.split("\n");
        return result;
    }

    private String[] spiltPoemTextByPunctuation(String poemText) {
        String result[];
        result = poemText.split("、|，|。|；|？|！");
        return result;
    }
}
