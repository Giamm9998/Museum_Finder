package model;

import java.util.ArrayList;

public class Museum {
    //required parametrs
    private final int museumId;
    private final String name;
    //optional parameters
    private final String wikiLink;
    private final String website;
    private final String location;
    private final double lat;
    private final double lng;
    private final String description;
    private final String address;
    private final String categories;
    private final String imageUrl;
    private ArrayList<Review> reviews;
    private ArrayList<Event> events;
    private float ticketPrice;

    public Museum(Builder builder) {
        museumId = builder.museumId;
        name = builder.name;
        wikiLink = builder.wikiLink;
        website = builder.website;
        location = builder.location;
        lat = builder.lat;
        lng = builder.lng;
        description = builder.description;
        address = builder.address;
        categories = builder.categories;
        imageUrl = builder.imageUrl;
    }

    public String getName() {
        return name;
    }

    public int getMuseumId() {
        return museumId;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public String getWebsite() {
        return website;
    }

    public String getLocation() {
        return location;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getCategories() {
        return categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public static class Builder {
        //required parametrs
        private final int museumId;
        private final String name;
        //optional parameters
        private String wikiLink;
        private String website;
        private String location;
        private double lat;
        private double lng;
        private String description;
        private String address;
        private String categories;
        private String imageUrl;

        public Builder(int museumId, String name) {
            this.museumId = museumId;
            this.name = name;
        }

        public Builder wikiLink(String wikiLink) {
            this.wikiLink = wikiLink;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder categories(String categories) {
            this.categories = categories;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder lat(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder lng(double lng) {
            this.lng = lng;
            return this;
        }

        public Museum build() {
            return new Museum(this);
        }
    }
}
