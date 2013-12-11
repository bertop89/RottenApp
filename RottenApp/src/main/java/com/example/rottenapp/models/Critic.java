package com.example.rottenapp.models;

/**
 * Created by alberto on 10/12/13.
 */
public class Critic {

    private String critic, freshness, publication, quote;
    private Links links;

    public Critic(){}

    public String getCritic() {
        return critic;
    }

    public String getFreshness() {
        return freshness;
    }

    public String getPublication() {
        return publication;
    }

    public String getQuote() {
        return quote;
    }

    public Links getLinks() {
        return links;
    }

    public class Links {
        private String review;

        public String getReview() {
            return review;
        }
    }

}
