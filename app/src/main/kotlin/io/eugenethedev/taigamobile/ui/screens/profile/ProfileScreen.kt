package io.eugenethedev.taigamobile.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.entities.Stats
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun ProfileScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
    userId: Long
) {
    val viewModel: ProfileViewModel = viewModel()
    viewModel.onOpen(userId)

    val currentUser by viewModel.currentUser.collectAsState()
    currentUser.subscribeOnError(onError)
    val currentUserStats by viewModel.currentUserStats.collectAsState()
    currentUserStats.subscribeOnError(onError)
    val currentUserProjects by viewModel.currentUserProjects.collectAsState()
    currentUserProjects.subscribeOnError(onError)

    ProfileScreenContentTest(
        navigateBack = navController::popBackStack,
        currentUser = currentUser.data,
        currentUserStats = currentUserStats.data,
        currentUserProjects = currentUserProjects.data ?: emptyList(),
        isLoading = currentUser is LoadingResult || currentUserStats is LoadingResult || currentUserProjects is LoadingResult,
        isError = currentUser is ErrorResult || currentUserStats is ErrorResult || currentUserProjects is ErrorResult
    )
}

@Composable
fun ProfileScreenContentTest(
    navigateBack: () -> Unit = {},
    currentUser: User? = null,
    currentUserStats: Stats? = null,
    currentUserProjects: List<Project> = emptyList(),
    isLoading: Boolean = false,
    isError: Boolean = false
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    AppBarWithBackButton(
        title = { Text(stringResource(R.string.profile)) },
        navigateBack = navigateBack
    )

    when {
        isLoading || isError -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        else -> {
            ProfileScreenContent(
                currentUser = currentUser,
                currentUserStats = currentUserStats,
                currentUserProjects = currentUserProjects,
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfileScreenContent(
    currentUser: User? = null,
    currentUserStats: Stats? = null,
    currentUserProjects: List<Project> = emptyList()
) {
    Image(
        painter = rememberImagePainter(
            data = currentUser?.avatarUrl ?: R.drawable.default_avatar,
            builder = {
                error(R.drawable.default_avatar)
                crossfade(true)
            },
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
    )

    Spacer(Modifier.height(16.dp))

    Text(
        text = currentUser?.fullName ?: "Full name",
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(2.dp))

    Text(
        text = "@${currentUser?.username ?: "username"}",
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyLarge,
    )

    Spacer(Modifier.height(24.dp))

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ColumnTextData(currentUserStats?.totalNumProjects.toString(), stringResource(R.string.projects))
        ColumnTextData(
            currentUserStats?.totalNumClosedUserStories.toString(),
            stringResource(R.string.closed_user_story)
        )
        ColumnTextData(currentUserStats?.totalNumContacts.toString(), stringResource(R.string.contacts))
    }

    Spacer(Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.projects),
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(Modifier.height(12.dp))

    LazyColumn(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
        items(currentUserProjects) {
            ProjectItem(it)
            Spacer(Modifier.height(6.dp))
        }

        item {
            Spacer(Modifier.navigationBarsHeight(8.dp))
        }
    }
}

@Composable
private fun ColumnTextData(titleText: String, bodyText: String) = Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = titleText,
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(Modifier.height(2.dp))

    Text(
        text = bodyText,
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun ProjectItem(project: Project) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.6f)
    ) {
        Image(
            painter = rememberImagePainter(
                data = project.avatarUrl ?: R.drawable.default_avatar,
                builder = {
                    error(R.drawable.default_avatar)
                    crossfade(true)
                },
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            project.description?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    Spacer(Modifier.width(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.4f)
    ) {
        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = null
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = project.fansCount.toString()
            )
        }

        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_watch),
                contentDescription = null
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = project.watchersCount.toString()
            )
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProfileScreenPreview() {
    val currentUser = User(
        _id = 123,
        fullName = null,
        photo = null,
        bigPhoto = null,
        username = "@username",
        name = "Cool user",
        pk = null
    )
    val currentUserStats = Stats(
        roles = listOf(
            "Design",
            "Front",
        ),
        totalNumClosedUserStories = 4,
        totalNumContacts = 48,
        totalNumProjects = 3
    )
    val currentUserProjects = listOf(
        Project(
            id = 1,
            name = "Cool project1",
            slug = "slug",
            description = "Cool description1",
            fansCount = 10,
            watchersCount = 3
        ),
        Project(
            id = 2,
            name = "Cool project2",
            slug = "slug",
            description = "Cool description2",
            fansCount = 1,
            watchersCount = 4
        ),
        Project(
            id = 3,
            name = "Cool project3",
            slug = "slug",
            description = "Cool description3",
            fansCount = 99,
            watchersCount = 0
        )
    )

    ProfileScreenContent(
        currentUser = currentUser,
        currentUserStats = currentUserStats,
        currentUserProjects = currentUserProjects
    )
}