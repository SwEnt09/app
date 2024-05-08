package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    val associationsSubscriptions =
        userProfileViewModel.userProfile.collectAsState().value.associationsSubscriptions.toList()
    AssociationListScreen(
        title = stringResource(R.string.association_subscription_screen_title),
        onBackButtonClicked = { navigationActions.goBack() },
        associationList = associationsSubscriptions,
        hasActionButton = true,
        actionButtonName = stringResource(R.string.association_subscription_screen_unfollow),
        onActionButtonClicked = {
            val newAssociationSubscriptions =
                associationsSubscriptions.filter { association -> association != it }
            userProfileViewModel.setUserProfile(
                userProfileViewModel.userProfile.value.copy(
                    associationsSubscriptions = newAssociationSubscriptions.toSet()
                )
            )
        },
        displayDescription = true
    )
}
