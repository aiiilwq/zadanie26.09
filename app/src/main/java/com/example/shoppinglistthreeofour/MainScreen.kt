package com.example.shoppinglistthreeofour

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen() {
    var isAdding by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(1) }
    var listOfShoppingItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var editingItem by remember { mutableStateOf<ShoppingItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isAdding) {
            AddDialog(
                id = index,
                onDismissRequest = { isAdding = false },
                onCompleteDialog = {
                    listOfShoppingItems += it
                    index++
                    isAdding = false
                }
            )
        }

        if (isEditing && editingItem != null) {
            EditDialog(
                shoppingItem = editingItem!!,
                onEditDone = { title, description, quantity ->
                    listOfShoppingItems = listOfShoppingItems.map {
                        if (it.id == editingItem!!.id) it.copy(title = title, description = description, quantity = quantity) else it
                    }
                    isEditing = false
                    editingItem = null
                },
                onDismissRequest = { isEditing = false; editingItem = null }
            )
        }

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = { isAdding = true }
        ) {
            Text("Добавить")
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(listOfShoppingItems) { element ->
                ShoppingItemCard(
                    shoppingItem = element,
                    onEditClicked = {
                        editingItem = element
                        isEditing = true
                    },
                    onDeleteClicked = {
                        listOfShoppingItems -= it
                    }
                )
            }
        }
    }
}

@Composable
fun ShoppingItemCard(
    shoppingItem: ShoppingItem,
    onDeleteClicked: (ShoppingItem) -> Unit,
    onEditClicked: (ShoppingItem) -> Unit
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = CardDefaults.cardColors().copy(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shoppingItem.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = shoppingItem.description,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = shoppingItem.quantity.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.width(16.dp))
            Column {
                IconButton(onClick = { onEditClicked(shoppingItem) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDeleteClicked(shoppingItem) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialog(id: Int, onCompleteDialog: (ShoppingItem) -> Unit, onDismissRequest: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Добавить элемент", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            TextField(
                label = { Text("Название") },
                value = title,
                onValueChange = { title = it }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                label = { Text("Описание") },
                value = description,
                onValueChange = { description = it }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                label = { Text("Количество") },
                value = quantity.toString(),
                onValueChange = { quantity = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { onDismissRequest() }) { Text("Отмена") }
                Button( colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Transparent),
                    onClick = {
                    onCompleteDialog(ShoppingItem(id, title, description, quantity))
                    onDismissRequest()
                }) { Text("Добавить") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialog(shoppingItem: ShoppingItem, onEditDone: (String, String, Int) -> Unit, onDismissRequest: () -> Unit) {
    var editedTitle by remember { mutableStateOf(shoppingItem.title) }
    var editedDescription by remember { mutableStateOf(shoppingItem.description) }
    var editedQuantity by remember { mutableIntStateOf(shoppingItem.quantity) }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Редактировать элемент", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            TextField(
                label = { Text("Название") },
                value = editedTitle,
                onValueChange = { editedTitle = it }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                label = { Text("Описание") },
                value = editedDescription,
                onValueChange = { editedDescription = it }
            )
            Spacer(Modifier.height(8.dp))
            TextField(
                label = { Text("Количество") },
                value = editedQuantity.toString(),
                onValueChange = { editedQuantity = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { onDismissRequest() }) { Text("Отмена") }
                Button(onClick = {
                    onEditDone(editedTitle, editedDescription, editedQuantity)
                    onDismissRequest()
                }) { Text("Сохранить") }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    MainScreen()
}