/*
 * PlusMinusBox.java
 * Created on May 4, 2008
 */
package sequor.control;

import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import sequor.model.BoundedRangeModel;

/**
 * <p>
 * PlusMinusBox represemts a box with plus/minus (or left/right, etc.)
 * buttons, which are normally used in conjunction with an underlying
 * BoundedRangeModel, toincrement/decrement the model.
 * </p>
 * @author Elisha Peterson
 */
public class PlusMinusBox extends ButtonBox {

    BoundedRangeModel model;

    public PlusMinusBox(int x, int y, BoundedRangeModel model) {
        this(x, y, 14, model);
    }

    public PlusMinusBox(int x, int y, int buttonSize, BoundedRangeModel model) {
        this(x, y, buttonSize, LAYOUT_BOX, model);
    }

    public PlusMinusBox(int x, int y, BoundedRangeModel model, int orientation) {
        this(x, y, model);
        setOrientation(orientation);
    }

    public PlusMinusBox(int x, int y, int buttonSize, int layout, BoundedRangeModel model) {
        super(x, y, 80, 22, layout);
        this.buttonSize = buttonSize;
        this.model = model;
        model.addChangeListener(this);
        initButtons();
        stateChanged(new ChangeEvent(model));
    }

    void initButtons() {
        add(new VisualButton("minus", null, BoundedShape.PLAY_TRIANGLE.rotated(90)));
        add(new VisualButton("plus", null, BoundedShape.PLAY_TRIANGLE.rotated(-90)));
        buttonStyle.setValue(STYLE_RBOX);
        adjustBounds();
        performLayout();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("plus")) {
            model.increment(false);
        } else if (e.getActionCommand().equals("minus")) {
            model.increment(false, -1);
        } else {
            super.actionPerformed(e);
        }
    }
}
