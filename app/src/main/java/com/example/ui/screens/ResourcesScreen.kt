package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResourceItem
import com.example.ui.components.EmptyState
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandDanger

@Composable
fun ResourcesScreen(
    resources: List<ResourceItem>,
    unitNameHelper: (String) -> String,
    onAddResource: () -> Unit,
    onDeleteResource: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.testTag("resources_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddResource,
                containerColor = BrandAccent,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_resource_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add resource")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Useful links, references, videos and documents",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Resources",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (resources.isNotEmpty()) {
                    items(resources, key = { it.id }) { res ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("resource_row_${res.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = res.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${unitNameHelper(res.unitId)} • ${res.type} • ${res.url}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        try {
                                            val uri = Uri.parse(if (!res.url.startsWith("http://") && !res.url.startsWith("https://")) "https://${res.url}" else res.url)
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    },
                                    modifier = Modifier.testTag("open_resource_${res.id}")
                                ) {
                                    Text("Open", color = BrandAccent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandAccent)
                                }

                                IconButton(
                                    onClick = { onDeleteResource(res.id) },
                                    modifier = Modifier.size(36.dp).testTag("delete_resource_${res.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete resource", tint = BrandDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    item {
                        EmptyState(message = "No resources saved yet.")
                    }
                }
            }
        }
    }
}
