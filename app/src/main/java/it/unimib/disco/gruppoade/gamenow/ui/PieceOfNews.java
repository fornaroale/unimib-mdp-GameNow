package it.unimib.disco.gruppoade.gamenow.ui;

import org.threeten.bp.LocalDateTime;

public class PieceOfNews implements Comparable<PieceOfNews> {
    private String title;
    private String desc;
    private String link;
    private LocalDateTime pubDate;
    private String image;
    private NewsProvider provider;

    public PieceOfNews(String title, String desc, String link, LocalDateTime pubDate, String image, NewsProvider provider) {
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.pubDate = pubDate;
        this.image = image;
        this.provider = provider;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public NewsProvider getProvider() {
        return provider;
    }

    @Override
    public int compareTo(PieceOfNews o) {
        return getPubDate().compareTo(o.getPubDate());
    }
}

