package org.benja.tpu_tsb_2.support;

public class Serie {
    private String title;
    private String seriesRuntime;
    private String certificate;
    private String episodesRuntime;
    private float imdbRating;
    private String overview;
    private String star1;
    private String star2;
    private String star3;
    private String star4;
    private int votes;


    public Serie(String title, String seriesRuntime, String certificate, String episodesRuntime, float imdbRating, String overview, String star1, String star2, String star3, String star4, int votes) {
        this.title = title;
        this.seriesRuntime = seriesRuntime;
        this.certificate = certificate;
        this.episodesRuntime = episodesRuntime;
        this.imdbRating = imdbRating;
        this.overview = overview;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.votes = votes;
    }

    public Serie(String[] dataRow) {
        this.title = dataRow[0];
        this.seriesRuntime = dataRow[1];
        this.certificate = dataRow[2];
        this.episodesRuntime = dataRow[3];
        this.imdbRating = Float.parseFloat(dataRow[5]);
        this.overview = dataRow[6];
        this.star1 = dataRow[7];
        this.star2 = dataRow[8];
        this.star3 = dataRow[9];
        this.star4 = dataRow[10];
        this.votes = Integer.parseInt(dataRow[11]);
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public String getSeriesRuntime() {
        return seriesRuntime;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getEpisodesRuntime() {
        return episodesRuntime;
    }

    public float getImdbRating() {
        return imdbRating;
    }

    public String getOverview() {
        return overview;
    }

    public String getStar1() {
        return star1;
    }

    public String getStar2() {
        return star2;
    }

    public String getStar3() {
        return star3;
    }

    public String getStar4() {
        return star4;
    }

    public int getVotes() {
        return votes;
    }
}
