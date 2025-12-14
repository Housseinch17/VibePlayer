package com.example.vibeplayer.feature.scan_music.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vibeplayer.R
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerIconShape
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerPrimaryButton
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerSnackbar
import com.example.vibeplayer.core.presentation.designsystem.components.VibePlayerTextWithTwoRadioButtons
import com.example.vibeplayer.core.presentation.designsystem.components.rotationIfScanning
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerIcons
import com.example.vibeplayer.core.presentation.designsystem.theme.VibePlayerImages
import com.example.vibeplayer.core.presentation.designsystem.theme.bodyLargeMedium
import com.example.vibeplayer.core.presentation.designsystem.theme.textPrimary

@Composable
fun ScanMusicScreen(
    modifier: Modifier = Modifier,
    scanMusicUi: ScanMusicUi,
    onActions: (ScanMusicActions) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(scanMusicUi.snackbarMessage) {
        if (scanMusicUi.snackbarMessage != null) {
            snackBarHostState.showSnackbar(
                message = scanMusicUi.snackbarMessage.asString(context = context),
                withDismissAction = false
            )
        }
    }

    Scaffold(
        modifier = modifier.padding(horizontal = 16.dp),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    text = stringResource(R.string.scan_music),
                    style = MaterialTheme.typography.bodyLargeMedium.copy(
                        color = MaterialTheme.colorScheme.textPrimary,
                        textAlign = TextAlign.Center
                    )
                )

                VibePlayerIconShape(
                    modifier = Modifier,
                    imageVector = VibePlayerIcons.ArrowLeft,
                    iconDescription = stringResource(R.string.back),
                    isEnabled = !scanMusicUi.isScanning,
                    onClick = {
                        onActions(ScanMusicActions.NavigateBack)
                    },
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    VibePlayerSnackbar(
                        snackbarData = data
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val rotationEveryTwoSeconds = rotationIfScanning(isScanning = scanMusicUi.isScanning)
            Image(
                modifier = Modifier
                    .size(140.dp)
                    .rotate(rotationEveryTwoSeconds),
                painter = VibePlayerImages.ScanningRadarImage,
                contentDescription = stringResource(R.string.radar)
            )

            DurationAndSizeComposable(
                modifier = Modifier.fillMaxWidth(),
                durationList = scanMusicUi.durationList,
                sizeList = scanMusicUi.sizeList,
                selectedDuration = scanMusicUi.selectedDuration,
                selectedSize = scanMusicUi.selectedSize,
                isEnabled = !scanMusicUi.isScanning,
                selectDuration = { duration ->
                    onActions(ScanMusicActions.SelectDuration(duration))
                },
                selectSize = { size ->
                    onActions(ScanMusicActions.SelectSize(size))
                }
            )

            VibePlayerPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                text = stringResource(R.string.scan),
                isEnabled = !scanMusicUi.isScanning,
                isScanning = scanMusicUi.isScanning,
                onclick = {
                    onActions(ScanMusicActions.Scan)
                }
            )
        }
    }
}

@Composable
fun DurationAndSizeComposable(
    modifier: Modifier = Modifier,
    durationList: List<String>,
    sizeList: List<String>,
    selectedDuration: String,
    selectedSize: String,
    isEnabled: Boolean,
    selectDuration: (String) -> Unit,
    selectSize: (String) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        VibePlayerTextWithTwoRadioButtons(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.ignore_duration_less_than),
            firstRadioText = durationList.first(),
            secondRadioText = durationList.last(),
            selectedRadio = selectedDuration,
            abbreviation = "s",
            isEnabled = isEnabled,
            selectRadio = selectDuration,
        )

        VibePlayerTextWithTwoRadioButtons(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = stringResource(R.string.ignore_size_less_than),
            firstRadioText = sizeList.first(),
            secondRadioText = sizeList.last(),
            selectedRadio = selectedSize,
            abbreviation = "KB",
            isEnabled = isEnabled,
            selectRadio = selectSize,
        )
    }
}
