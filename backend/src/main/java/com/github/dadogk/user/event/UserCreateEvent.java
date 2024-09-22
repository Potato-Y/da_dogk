package com.github.dadogk.user.event;

import com.github.dadogk.user.entity.User;

public class UserCreateEvent {

  private User user;

  public UserCreateEvent(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }
}
