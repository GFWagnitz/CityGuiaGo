package com.pi.cityguiago

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pi.cityguiago.module.Login.LoginEffect
import com.pi.cityguiago.module.Login.LoginEvent
import com.pi.cityguiago.module.Login.LoginViewModel
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginView(
    navController: NavHostController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect {
            when (it) {
                is LoginEffect.LoginSuccess -> navController.navigate("home")
                is LoginEffect.ShowErrorMessage -> {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Metrics.Margins.default),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Bom te ver de novo",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.small))
        Text(
            text = "Faça seu login no app",
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.huge))

        Column(modifier = Modifier.fillMaxWidth()) {
            TextFieldWithTitle(
                title = "Email",
                placeholder = "Digite seu email",
                text = email,
                onTextChanged = { email = it }
            )
            Spacer(modifier = Modifier.height(Metrics.Margins.default))
            TextFieldWithTitle(
                title = "Senha",
                placeholder = "Sua senha aqui",
                text = password,
                onTextChanged = { password = it },
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.height(Metrics.Margins.huge))

        PrimaryButton(
            text = "Entrar",
//            onClick = { navController.navigate("home") },
            onClick = { viewModel.onEvent(LoginEvent.Login(email, password)) },
            icon = Icons.Default.ArrowForward
        )

        Spacer(modifier = Modifier.height(Metrics.Margins.small))

        Text(
            text = "Faça seu cadastro",

            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { navController.navigate("register") }
        )
    }
}
