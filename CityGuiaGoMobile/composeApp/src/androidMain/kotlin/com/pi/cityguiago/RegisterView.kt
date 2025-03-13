package com.pi.cityguiago

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavHostController
import com.pi.cityguiago.module.Register.RegisterEffect
import com.pi.cityguiago.module.Register.RegisterEvent
import com.pi.cityguiago.module.Register.RegisterViewModel
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.PrimaryButton
import com.pi.cityguiago.designsystem.components.TextFieldWithTitle
import com.pi.cityguiago.network.PrefCacheManager
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterView(
    navController: NavHostController,
    store: PrefCacheManager,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect {
            when (it) {
                is RegisterEffect.RegisterSuccess -> navController.navigate("home")
                is RegisterEffect.ShowErrorMessage -> {
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
            text = "Faça seu cadastro no app!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.huge))

        TextFieldWithTitle(
            title = "Qual o seu nome completo?",
            placeholder = "Nome completo aqui",
            text = fullName,
            onTextChanged = { fullName = it }
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.default))

        TextFieldWithTitle(
            title = "Qual o seu email?",
            placeholder = "Insira seu email aqui",
            text = email,
            onTextChanged = { email = it }
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.default))

        TextFieldWithTitle(
            title = "Senha",
            placeholder = "Mínimo 8 caracteres",
            text = password,
            onTextChanged = { password = it },
            isPassword = true
        )
        Spacer(modifier = Modifier.height(Metrics.Margins.default))

        TextFieldWithTitle(
            title = "Repita a Senha",
            placeholder = "Repita a senha aqui",
            text = confirmPassword,
            onTextChanged = { confirmPassword = it },
            isPassword = true
        )

        Spacer(modifier = Modifier.height(Metrics.Margins.huge))

        PrimaryButton(
            text = "Cadastrar",
            onClick = { viewModel.onEvent(RegisterEvent.Register(fullName, email, password, confirmPassword) )}
        )

        Spacer(modifier = Modifier.height(Metrics.Margins.small))

        Text(
            text = "Já é cadastrado? Entre aqui",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { navController.popBackStack() }
        )
    }
}

