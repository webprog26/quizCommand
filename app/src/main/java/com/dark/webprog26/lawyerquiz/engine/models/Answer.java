package com.dark.webprog26.lawyerquiz.engine.models;

import java.io.Serializable;

/**
 * Answer POJO class
 */

public class Answer implements Serializable{

    //Fields declared as public. It hurts the encapsulation principle,
    // but necessary to fill them with values directly from FirebaseDatabase
    public long id;
    public String mAnswerText;
    public boolean isCorrect;
    public long nextQuestionId;
    public double mPoints;
    public String referenceText;

    //Default empty constructor is another FirebaseDatabase condition
    public Answer() {
    }

    public Answer(long id, String mAnswerText, double mPoints, long nextQuestionId, String referenceText) {
        this.id = id;
        this.mAnswerText = mAnswerText;
        this.mPoints = mPoints;
        this.nextQuestionId = nextQuestionId;
        this.referenceText = referenceText;
    }

    public String getAnswerText() {
        return mAnswerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNextQuestionId() {
        return nextQuestionId;
    }

    public double getPoints() {
        return mPoints;
    }

    public void setPoints(double points) {
        this.mPoints = points;
    }

    public void setNextQuestionId(long nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }

    public String getReferenceText() {
        return referenceText;
    }

    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
    }

    @Override
    public String toString() {
        return "Answer " + getId() + "\n"
                + "points " + getPoints() + "\n"
                + "next question id " + getNextQuestionId();
    }
}
