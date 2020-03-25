package com.example.android.navigation;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class GameOverFragmentDirections {
  private GameOverFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionGameOverFragmentToGameFragment() {
    return new ActionOnlyNavDirections(R.id.action_gameOverFragment_to_gameFragment);
  }
}
