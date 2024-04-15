package com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Contact")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	private String cname;
	private String cnickname;
	private String cwork;
	private String cemail;
	private String cphone;
	private String cimage;
	private String cpostalcode;
	private String ccanadaprovince;
	private String caddressstreet;
	private String caddresscity;
	private int caddresscivic;
	private String caddressunit;
	private String cwebsite;
	
	@ManyToOne
	@JsonIgnore // for search
	private Userr userr;

	
	@Column(length = 100000)
	private String cdescription;
	
	public Contact(String cname, String cnickname, String cwork, String cemail, String cphone, String cimage,
			String cpostalcode, String ccanadaprovince, String caddressstreet, String caddresscity,
			int caddresscivic, String caddressunit, String cwebsite, String cdescription, Userr userr) {
		super();
		this.cname = cname;
		this.cnickname = cnickname;
		this.cwork = cwork;
		this.cemail = cemail;
		this.cphone = cphone;
		this.cimage = cimage;
		this.cpostalcode = cpostalcode;
		this.ccanadaprovince = ccanadaprovince;
		this.caddressstreet = caddressstreet;
		this.caddresscity = caddresscity;
		this.caddresscivic = caddresscivic;
		this.caddressunit = caddressunit;
		this.cwebsite = cwebsite;
		this.cdescription = cdescription;
		this.userr = userr;
	}


	public Contact() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", cname=" + cname + ", cnickname=" + cnickname + ", cwork=" + cwork
				+ ", cemail=" + cemail + ", cphone=" + cphone + ", cimage=" + cimage + ", cpostalcode=" + cpostalcode
				+ ", ccanadaprovince=" + ccanadaprovince + ", caddressstreet=" + caddressstreet + ", caddresscity="
				+ caddresscity + ", caddresscivic=" + caddresscivic + ", caddressunit=" + caddressunit + ", cwebsite="
				+ cwebsite + ", cdescription=" + cdescription + ", userr=" + userr + "]";
	}
	
	@Override
	public boolean equals(Object obj)                // use this when using orphanRemoval
	{
		return this.cid == ((Contact)obj).getCid();
	}
	

	public String getCname() {
		return cname;
	}


	public void setCname(String cname) {
		this.cname = cname;
	}


	public String getCnickname() {
		return cnickname;
	}


	public void setCnickname(String cnickname) {
		this.cnickname = cnickname;
	}


	public String getCwork() {
		return cwork;
	}


	public void setCwork(String cwork) {
		this.cwork = cwork;
	}


	public String getCemail() {
		return cemail;
	}


	public void setCemail(String cemail) {
		this.cemail = cemail;
	}


	public String getCphone() {
		return cphone;
	}


	public void setCphone(String cphone) {
		this.cphone = cphone;
	}


	public String getCimage() {
		return cimage;
	}


	public void setCimage(String cimage) {
		this.cimage = cimage;
	}


	public String getCpostalcode() {
		return cpostalcode;
	}


	public void setCpostalcode(String cpostalcode) {
		this.cpostalcode = cpostalcode;
	}


	public String getCcanadaprovince() {
		return ccanadaprovince;
	}


	public void setCcanadaprovince(String ccanadaprovince) {
		this.ccanadaprovince = ccanadaprovince;
	}


	public String getCaddressstreet() {
		return caddressstreet;
	}


	public void setCaddressstreet(String caddressstreet) {
		this.caddressstreet = caddressstreet;
	}


	public String getCaddresscity() {
		return caddresscity;
	}


	public void setCaddresscity(String caddresscity) {
		this.caddresscity = caddresscity;
	}


	public int getCaddresscivic() {
		return caddresscivic;
	}


	public void setCaddresscivic(int caddresscivic) {
		this.caddresscivic = caddresscivic;
	}


	public String getCaddressunit() {
		return caddressunit;
	}


	public void setCaddressunit(String caddressunit) {
		this.caddressunit = caddressunit;
	}


	public String getCwebsite() {
		return cwebsite;
	}


	public void setCwebsite(String cwebsite) {
		this.cwebsite = cwebsite;
	}


	public String getCdescription() {
		return cdescription;
	}


	public void setCdescription(String cdescription) {
		this.cdescription = cdescription;
	}


	public Userr getUserr() {
		return userr;
	}


	public void setUserr(Userr userr) {
		this.userr = userr;
	}


	public int getCid() {
		return cid;
	}


	public void setCid(int cid) {
		this.cid = cid;
	}

}
