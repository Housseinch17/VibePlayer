package com.example.vibeplayer.feature.permission.presentation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import com.example.vibeplayer.R
import com.example.vibeplayer.app.presentation.MainActivity
import com.example.vibeplayer.core.presentation.designsystem.components.VibePLayerLifecycleEventListener
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerDialog
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyMediumRegular
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary
import com.example.vibeplayer.core.presentation.designsystem.theme.textSecondary

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    permissionUiState: PermissionUiState,
    onActions: (PermissionActions) -> Unit,
) {
    val context = LocalContext.current
    //this is lightweight since using context as MainActivity and not activity as MainActivity
    val activity = context as MainActivity

    //permission to request
    val permission = activity.permission

    VibePLayerLifecycleEventListener { events ->
        when (events) {
            Lifecycle.Event.ON_RESUME -> {
                val hasGranted = activity.checkMediaPermission()
                if (hasGranted) {
                    onActions(PermissionActions.NavigateMainPage)
                }
            }
            else -> {}
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasGranted ->
            if (hasGranted) {
                onActions(PermissionActions.NavigateMainPage)
            } else {
                //Check if permanently denied
                //this will set automatically true if the user deny permission twice since
                //it's handled internally by android studio
                val permanentlyDenied =
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                if (permanentlyDenied) {
                    onActions(PermissionActions.ShowDialog(true))
                }
            }
        },
    )

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(56.dp),
                imageVector = VibePlayerIcons.Logo,
                contentDescription = stringResource(R.string.logo_icon)
            )

            Text(
                modifier = Modifier.padding(top = 28.dp),
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.textPrimary
                )
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.access_needed_string),
                style = MaterialTheme.typography.bodyMediumRegular.copy(
                    color = MaterialTheme.colorScheme.textSecondary,
                )
            )

            VibePlayerPrimaryButton (
                modifier = Modifier.padding(top = 20.dp),
                text = stringResource(R.string.allow_access),
            ) {
                permissionLauncher.launch(permission)
            }
        }
        if (permissionUiState.showDialog) {
            VibePlayerDialog(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.permission_denied),
                text = stringResource(R.string.dialog_message),
                confirmText = stringResource(R.string.try_again),
                dismissText = stringResource(R.string.ok),
                confirmButtonClick = {
                    onActions(PermissionActions.ShowDialog(false))
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                },
                dismissButtonClick = {
                    onActions(PermissionActions.ShowDialog(false))
                }
            )
        }
    }
}