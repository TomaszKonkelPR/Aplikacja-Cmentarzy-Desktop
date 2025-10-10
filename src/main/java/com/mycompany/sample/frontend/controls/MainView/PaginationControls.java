package com.mycompany.sample.frontend.controls.MainView;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PaginationControls {
    private int currentPageIndex = 0;
    private long totalRecords = 0;
    private int pageSize = 50;

    private final Button prevButton = new Button("<");
    private final Button nextButton = new Button(">");
    private final Label currentPageLabel = new Label("Strona 0/0");

    public void reset(int totalRecords) {
        this.totalRecords = totalRecords;
        this.currentPageIndex = 0;
        updateLabel();
    }

    public void nextPage() {
        long totalPages = (totalRecords + pageSize - 1) / pageSize;
        if (totalPages == 0)
            return;

        currentPageIndex = (currentPageIndex + 1) % (int) totalPages;
        updateLabel();
    }

    public void prevPage() {
        long totalPages = (totalRecords + pageSize - 1) / pageSize;
        if (totalPages == 0)
            return;

        currentPageIndex = (currentPageIndex - 1 + (int) totalPages) % (int) totalPages;
        updateLabel();
    }

    public void updateLabel() {
        long totalPages = (totalRecords + pageSize - 1) / pageSize;
        currentPageLabel.setText("Strona " + (currentPageIndex + 1) + "/" + totalPages);
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Button getPrevButton() {
        return prevButton;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Label getCurrentPageLabel() {
        return currentPageLabel;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
