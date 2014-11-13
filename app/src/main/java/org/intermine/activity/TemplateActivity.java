package org.intermine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import org.intermine.R;
import org.intermine.core.model.Model;
import org.intermine.core.templates.SwitchOffAbility;
import org.intermine.core.templates.Template;
import org.intermine.core.templates.constraint.Constraint;
import org.intermine.core.templates.constraint.ConstraintOperation;
import org.intermine.core.templates.constraint.PathConstraintAttribute;
import org.intermine.util.Collections;
import org.intermine.view.AttributeConstraintView;
import org.intermine.view.LookupConstraintView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Daria Komkova <Daria_Komkova @ hotmail.com>
 */
public class TemplateActivity extends BaseActivity {
    public static final String TEMPLATE_KEY = "template_key";
    public static final String MINE_NAME_KEY = "mine_name_key";

    @InjectView(R.id.constraints_container)
    ViewGroup mContainer;

    @InjectView(R.id.template_description)
    TextView mTemplateDescription;

    private Template mTemplate;
    private String mMineName;

    // --------------------------------------------------------------------------------------------
    // Static Methods
    // --------------------------------------------------------------------------------------------

    public static void start(Context context, Template template, String mineName) {
        Intent intent = new Intent(context, TemplateActivity.class);
        intent.putExtra(TEMPLATE_KEY, template);
        intent.putExtra(MINE_NAME_KEY, mineName);
        context.startActivity(intent);
    }

    // --------------------------------------------------------------------------------------------
    // Fragment Lifecycle
    // --------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_activity);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);

        mTemplate = getIntent().getParcelableExtra(TEMPLATE_KEY);
        mMineName = getIntent().getStringExtra(MINE_NAME_KEY);

        if (null != mTemplate) {
            setTitle(mTemplate.getTitle());
            mTemplate.setDescription(mTemplate.getDescription());

            processConstraints(mTemplate.getConstraints());
        }
    }

    // --------------------------------------------------------------------------------------------
    // Callbacks
    // --------------------------------------------------------------------------------------------

    @OnClick(R.id.show_results)
    protected void showTemplatesResults() {
        TemplateResultsActivity.start(this, mTemplate);
    }

    // --------------------------------------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------------------------------------

    protected void processConstraints(List<Constraint> constraints) {
        List<Constraint> editableConstraints = Collections.newArrayList();

        Model model = getStorage().getMineModel(mMineName);

        for (Constraint constraint : constraints) {
            if (!SwitchOffAbility.OFF.equals(constraint.getSwitched())) {
                ConstraintOperation operation = ConstraintOperation.valueOf(constraint.getOp());

                if (ConstraintOperation.LOOKUP.equals(operation)) {
                    LookupConstraintView view = new LookupConstraintView(this, constraint.getValue());
                    mContainer.addView(view);
                } else if (PathConstraintAttribute.VALID_OPS.contains(operation)) {
                    AttributeConstraintView view = new AttributeConstraintView(this, constraint.getValue());
                    mContainer.addView(view);
                }

                Log.e("ddd", constraint.getPath());
            }
        }
    }

    private final String[] INTEGRAL_TYPES = {"int", "Integer", "long", "Long"};
    private final String[] FRACTIONAL_TYPES = {"double", "Double", "float", "Float"};
    private final String[] NUMERIC_TYPES = {"int", "Integer", "long", "Long", "double", "Double", "float", "Float"};
    private final String[] BOOLEAN_TYPES = {"boolean", "Boolean"};
}