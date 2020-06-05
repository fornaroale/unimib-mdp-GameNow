package it.unimib.disco.gruppoade.gamenow.models;

import org.threeten.bp.LocalDateTime;

public class PieceOfNews implements Comparable<PieceOfNews> {
    private String title;
    private String desc;
    private String link;
    private LocalDateTime pubDate;
    private String image;
    private String guid;
    private NewsProvider provider;

    public PieceOfNews(String title, String desc, String link, LocalDateTime pubDate, String image, String guid, NewsProvider provider) {
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.pubDate = pubDate;
        this.image = image;
        this.guid = guid;
        this.provider = provider;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public String getImage() {
        return image;
    }

    public NewsProvider getProvider() {
        return provider;
    }

    public String getGuid() {
        return guid;
    }

    @Override
    public int compareTo(PieceOfNews o) {
        return getPubDate().compareTo(o.getPubDate());
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof PieceOfNews)) return false;

        PieceOfNews o = (PieceOfNews) obj;
        return o.getGuid().equals(this.getGuid());
    }
}