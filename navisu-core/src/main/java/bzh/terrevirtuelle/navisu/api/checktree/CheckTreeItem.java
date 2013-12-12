package bzh.terrevirtuelle.navisu.api.checktree;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NaVisu
 *
 * @author tibus
 * @date 11/12/2013 22:20
 */
public interface CheckTreeItem {

    String text();
    void setText(String text);

    boolean selected();
    void setSelected(boolean selected);

    List<Action> getActions();
    void         addActions(Action... actions);

    public static class CheckTreeItemImpl implements CheckTreeItem {

        protected String text;
        protected BooleanProperty selected;
        protected List<Action> actions;

        {
            this.actions = new ArrayList<>();
        }

        public CheckTreeItemImpl() {
            this.text = "no-text";
            this.selected = new SimpleBooleanProperty(true);
        }

        public CheckTreeItemImpl(String text, boolean selected, Action... actions) {
            this.text = text;
            this.selected = new SimpleBooleanProperty(selected);
            this.actions.addAll(Arrays.asList(actions));
        }

        @Override
        public String text() {
            return this.text;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public boolean selected() {
            return this.selected.get();
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        @Override
        public List<Action> getActions() {
            return this.actions;
        }

        @Override
        public void addActions(Action... actions) {
            this.actions.addAll(Arrays.asList(actions));
        }
    }
}