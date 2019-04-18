package com.ef.wss.newscollect.pojo;

public class LinkType {
    private Integer id;

    private String url;

    private String typeS;
    
    public LinkType(String url,String type) {
    	this.url = url;
    	this.typeS =type;
    }
    

    public LinkType(Integer id, String url, String typeS) {
		super();
		this.id = id;
		this.url = url;
		this.typeS = typeS;
	}


	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getTypeS() {
        return typeS;
    }

    public void setTypeS(String typeS) {
        this.typeS = typeS == null ? null : typeS.trim();
    }
}