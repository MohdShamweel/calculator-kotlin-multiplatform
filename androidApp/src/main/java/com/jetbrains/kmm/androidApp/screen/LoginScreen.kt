package com.jetbrains.kmm.androidApp.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.jetbrains.androidApp.R

@Composable
fun LoginScreen(
    onLogin: (String) -> Unit,
) {
    val context = LocalContext.current

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
            val idToken = googleSignInAccount.idToken.orEmpty()
            onLogin(idToken)
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SignInWithGoogle(
            modifier = Modifier.align(Alignment.Center),
            onClick = {
                val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
                googleLauncher.launch(googleSignInClient.signInIntent)
            }
        )
    }

}


@Composable
fun SignInWithGoogle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .border(
                    width = 0.5.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(100)
                )
                .clickable { onClick() }
                .background(Color.White)
                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_google),
                contentDescription = null,
            )
            Text(
                text = "Sign in with Google",
                color = Color.Gray
            )
        }

    }
}