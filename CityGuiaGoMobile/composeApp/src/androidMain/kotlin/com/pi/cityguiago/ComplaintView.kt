package com.pi.cityguiago

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pi.cityguiago.designsystem.Background
import com.pi.cityguiago.designsystem.Metrics
import com.pi.cityguiago.designsystem.components.PrimaryButton
import com.pi.cityguiago.designsystem.components.TextBody1
import com.pi.cityguiago.designsystem.components.TextEditor
import com.pi.cityguiago.designsystem.components.TextH1
import com.pi.cityguiago.designsystem.components.TextH3
import com.pi.cityguiago.designsystem.components.VerticalSpacers
import com.pi.cityguiago.model.Complaint

@Composable
fun ComplaintView(
    navController: NavHostController,
    complaint: Complaint
) {
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextH3("Denúncia") },
                backgroundColor = Background,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(horizontal = Metrics.Margins.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {

            VerticalSpacers.Large()

            TextH1(complaint.title)

            VerticalSpacers.Large()

            TextBody1("Escrever denúncia para moderação")

            VerticalSpacers.Default()

            TextEditor(
                "Explique em detalhes o motivo pelo que acha que esse conteúdo deve ser removido do aplicativo",
                text = text,
                onTextChanged = { text = it }
            )
            VerticalSpacers.Default()

            PrimaryButton("Enviar", onClick = {})

            VerticalSpacers.Large()
        }
    }
}