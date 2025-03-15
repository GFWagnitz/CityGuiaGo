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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.pi.cityguiago.module.Login.LoginEffect
import com.pi.cityguiago.module.Login.LoginEvent
import com.pi.cityguiago.module.Login.LoginViewModel
import com.pi.cityguiago.designsystem.*
import com.pi.cityguiago.designsystem.components.*
import com.pi.cityguiago.network.PrefCacheManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginView(
    navController: NavHostController,
    store: PrefCacheManager,
    viewModel: LoginViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.LoginSuccess -> {
                    scope.launch {
                        store.saveUser(effect.user)
                        navController.navigate("home")
                    }
                }
                is LoginEffect.ShowErrorMessage -> {
                    Toast.makeText(context, effect.errorMessage, Toast.LENGTH_LONG).show()
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
            onClick = { viewModel.onEvent(LoginEvent.Login(email, password)) },
            icon = painterResource(id = R.drawable.ic_arrow_right)
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
