package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.user.UserProfileViewModel

// This screen is a list of membership to committees of associations
@Composable
fun AssociationCommitteeMemberScreen(
    userProfileViewModel: UserProfileViewModel,
    navigationActions: NavigationActions
) {
    val associationsCommitteeMemberList =
        userProfileViewModel.userProfile.collectAsState().value.committeeMember.toList()
    AssociationListScreen(
        title = stringResource(R.string.association_committee_member_screen_title),
        onBackButtonClicked = { navigationActions.goBack() },
        associationList = associationsCommitteeMemberList,
        hasActionButton = false,
        onAssociationClicked = {
            // TODO: navigate to association detail screen
        }
    )
}
