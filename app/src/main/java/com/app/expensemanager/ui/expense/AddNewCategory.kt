package com.app.expensemanager.ui.expense

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.app.expensemanager.R
import com.app.expensemanager.data.models.UIEvent
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel


@Composable
fun AddNewCategoryScreen(viewModel: ExpenseViewModel, parentNavController: NavHostController) {

    var data by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.addCategoryResult.collect {
            when (it) {
                is NetworkResultState.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResultState.Success -> {
                    Toast.makeText(context, "Category added !!", Toast.LENGTH_SHORT).show()
                    viewModel.getCategories()
                    parentNavController.popBackStack()
                }
                else -> {

                }

            }
        }
    }

    Column(modifier = Modifier.padding(10.dp)) {
        OutlinedTextField(
            value = data,
            onValueChange = {
                data = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                },
            singleLine = true,
            textStyle = Typography.bodyLarge,
            placeholder = {
                Text(text = "New Category", style = Typography.bodyLarge)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onSecondary,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                disabledTextColor = MaterialTheme.colorScheme.onSecondary
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (viewModel.uiEventState.value == UIEvent.Idle)
                    viewModel.onUIEvent(UIEvent.AddCategory(data.trim()))
            }, modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple40))
        ) {
            Text(
                text = "Add Category",
                style = Typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
        }
    }
}

data class NewCategory(val user: String, val category: String)