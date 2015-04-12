package com.example.guestbook;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.Key;

import java.lang.String;
import java.util.Date;
import java.util.List;

/**
 * The @Entity tells Objectify about our entity.  We also register it in OfyHelper.java -- very
 * important. Our primary key @Id is set automatically by the Google Datastore for us.
 *
 * We add a @Parent to tell the object about its ancestor. We are doing this to support many
 * guestbooks.  Objectify, unlike the AppEngine library requires that you specify the fields you
 * want to index using @Index.  This is often a huge win in performance -- though if you don't Index
 * your data from the start, you'll have to go back and index it later.
 *
 * NOTE - all the properties are PUBLIC so that can keep this simple, otherwise,
 * Jackson, wants us to write a BeanSerializaer for cloud endpoints.
 **/
@Entity
public class NoteSet{
 
  @Parent Key<Guestbook> theBook;
  @Id public Long id;

  public String content;
  public List<Integer> commentTimeList;
  public List<String> commentContentList;
  public String youtubeID;

  /**
   * Simple constructor just sets the date
   **/
  public NoteSet() {
  }

}