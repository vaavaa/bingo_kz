package kz.regto.bingo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwoTextViews extends LinearLayout {
    private String label;
    private String field;
    private float size_text;
    private int color_text;

    private TextView label_text;
    private TextView field_text;


    public TwoTextViews(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initView();
    }

    public TwoTextViews(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView();
    }

    public TwoTextViews(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.twotextviews, null);
        label_text = (TextView) view.findViewById(R.id.label_text);
        field_text = (TextView) view.findViewById(R.id.field_text);
        label_text.setText(label);
        field_text.setText(field);
        label_text.setTextColor(color_text);
        field_text.setTextColor(color_text);
        label_text.setTextSize(size_text);
        field_text.setTextSize(size_text);


        addView(view);
    }
    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TwoTextViews, 0, 0);

        try {
            label = a.getString(R.styleable.TwoTextViews_label_text);
            field = a.getString(R.styleable.TwoTextViews_field_text);
            size_text = a.getDimensionPixelSize(R.styleable.TwoTextViews_size_text, 0);
            color_text=a.getColor(R.styleable.TwoTextViews_color_text, 0);

        } finally {
            a.recycle();
        }
    }

    public String getLabel() {
        return label;
    }

    public String getField() {
        return field;
    }

    public void setLabel(String label) {
        this.label = label;
        label_text.setText(label);
    }

    public void setField(String field) {
        this.field = field;
        field_text.setText(field);
    }
}