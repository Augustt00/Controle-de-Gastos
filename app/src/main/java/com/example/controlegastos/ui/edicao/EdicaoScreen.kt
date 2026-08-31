@@
@@
 package com.example.controlegastos.ui.edicao
 
-import java.util.Locale
+import java.text.NumberFormat
+import java.util.Locale
@@
-private val CorEdicao = Color(0xFF2F6F62)
+private val CorEdicao = Color(0xFF2F6F62)
@@
 private val CorBordaChip = Color(0xFFD0D7D3)
+
+private fun Long.formatarMoedaPtBr(): String {
+    return NumberFormat
+        .getCurrencyInstance(Locale("pt", "BR"))
+        .format(this / 100.0)
+}
@@
-                    2 -> {
-                        item {
-                            CabecalhoSecao(
-                                titulo = "Saldo e carteira",
-                                descricao = "Cadastre uma conta, dinheiro em carteira ou saldo reservado."
-                            )
-                        }
-
-                        item {
-                            FormularioContaSaldo(
-                                uiState = uiState,
-                                onInstituicaoSelecionada = viewModel::selecionarInstituicao,
-                                onTipoSelecionado = viewModel::selecionarTipoConta,
-                                onSaldoAlterado = viewModel::atualizarSaldoInicial,
-                                onSalvar = viewModel::salvarContaSaldo
-                            )
-                        }
-
-                        items(uiState.contas, key = { it.id }) { conta ->
-                            LinhaContaSaldo(
-                                conta = conta,
-                                onAtivacaoAlterada = { ativa ->
-                                    viewModel.alterarAtivacaoConta(conta, ativa)
-                                }
-                            )
-                        }
-                    }
+                    2 -> {
+                        item {
+                            CabecalhoSecao(
+                                titulo = "Saldo e carteira",
+                                descricao = "Cadastre uma conta, dinheiro em carteira ou saldo reservado."
+                            )
+                        }
+
+                        item {
+                            // cartão resumo do patrimônio ativo
+                            val contasAtivas = uiState.contas.filter { it.ativo }
+                            val totalAtivo = contasAtivas.sumOf { it.saldoCentavos }
+                            val quantidadeAtivas = contasAtivas.size
+
+                            PatrimonioTotalCard(
+                                totalAtivoCentavos = totalAtivo,
+                                contasAtivasCount = quantidadeAtivas
+                            )
+                        }
+
+                        item {
+                            FormularioContaSaldo(
+                                uiState = uiState,
+                                onInstituicaoSelecionada = viewModel::selecionarInstituicao,
+                                onTipoSelecionado = viewModel::selecionarTipoConta,
+                                onSaldoAlterado = viewModel::atualizarSaldoInicial,
+                                onSalvar = viewModel::salvarContaSaldo
+                            )
+                        }
+
+                        items(uiState.contas, key = { it.id }) { conta ->
+                            LinhaContaSaldo(
+                                conta = conta,
+                                onAtivacaoAlterada = { ativa ->
+                                    viewModel.alterarAtivacaoConta(conta, ativa)
+                                }
+                            )
+                        }
+                    }
@@
 }
+
+@Composable
+private fun PatrimonioTotalCard(
+    totalAtivoCentavos: Long,
+    contasAtivasCount: Int
+) {
+    Card(
+        shape = RoundedCornerShape(12.dp),
+        colors = CardDefaults.cardColors(containerColor = CorEdicao),
+        modifier = Modifier
+            .fillMaxWidth()
+            .height(112.dp)
+    ) {
+        Column(modifier = Modifier.padding(16.dp)) {
+            Text(
+                text = "PATRIMÔNIO TOTAL ATIVO",
+                color = Color.White.copy(alpha = 0.9f),
+                style = MaterialTheme.typography.labelSmall
+            )
+            Spacer(Modifier.height(8.dp))
+            Text(
+                text = totalAtivoCentavos.formatarMoedaPtBr(),
+                color = Color.White,
+                style = MaterialTheme.typography.headlineSmall,
+                fontWeight = FontWeight.Bold
+            )
+
+            Spacer(Modifier.height(10.dp))
+
+            Divider(color = Color.White.copy(alpha = 0.18f))
+
+            Spacer(Modifier.height(8.dp))
+
+            Row(verticalAlignment = Alignment.CenterVertically) {
+                Text(
+                    text = "$contasAtivasCount ${if (contasAtivasCount == 1) "conta ativa" else "contas ativas"}",
+                    color = Color.White.copy(alpha = 0.9f),
+                    style = MaterialTheme.typography.bodySmall
+                )
+                Spacer(Modifier.weight(1f))
+            }
+        }
+    }
+}
