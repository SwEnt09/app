package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.user.UserProfileViewModel

// This screen is a list of the subscriptions of a user to associations
@Composable
fun AssociationSubscriptionsScreen(
    userProfileViewModel: UserProfileViewModel,
    navigationActions: NavigationActions
) {
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val associationsSubscriptions = userProfile.associationsSubscriptions.toList()
    AssociationListScreen(
        title = stringResource(R.string.association_subscription_screen_title),
        onBackButtonClicked = { navigationActions.goBack() },
        associationList = associationsSubscriptions,
        hasActionButton = true,
        actionButtonName = stringResource(R.string.association_subscription_screen_unfollow),
        actionSnackbarMessage =
            stringResource(R.string.association_subscription_screen_snackbar_message),
        actionSnackbarUndoMessage =
            stringResource(R.string.association_subscription_screen_snackbar_undo),
        onActionButtonClicked = {
            val newAssociationSubscriptions =
                associationsSubscriptions.filter { association -> association != it }
            userProfileViewModel.setUserProfile(
                userProfileViewModel.userProfile.value.copy(
                    associationsSubscriptions = newAssociationSubscriptions.toSet()
                )
            )
        },
        onUndoActionButtonClicked = {
            userProfileViewModel.setUserProfile(
                userProfileViewModel.userProfile.value.copy(
                    associationsSubscriptions = (associationsSubscriptions + it).toSet()
                )
            )
        },
        onAssociationClicked = {
            // TODO: navigate to association detail screen
        }
    )
}
