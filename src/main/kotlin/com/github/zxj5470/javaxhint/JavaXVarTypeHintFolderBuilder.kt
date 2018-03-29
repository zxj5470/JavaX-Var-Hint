package com.github.zxj5470.javaxhint

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.Document
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author: zxj5470
 * @date: 2018/3/23
 */
class JavaXVarTypeHintFolderBuilder : FoldingBuilderEx() {
	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> =
		if (root.hasNoError) PsiTreeUtil.findChildrenOfType(root, PsiClass::class.java).flatMap {
			PsiTreeUtil.findChildrenOfType(root, PsiMethod::class.java).flatMap {
				PsiTreeUtil.findChildOfType(it, PsiCodeBlock::class.java)?.children?.mapNotNull {
					findVarDeclaration(it).let { (bool, psi, text) ->
						if (bool && psi != null) getFold(psi, text) else null
					}
				}.orEmpty()
			}
		}.toTypedArray() else emptyArray()

	private fun getFold(elem: PsiElement, placeHolder: String?) =
		NamedFoldingDescriptor(elem.node, elem.textRange, null, placeHolder.orEmpty())

	override fun getPlaceholderText(node: ASTNode) = "..."

	override fun isCollapsedByDefault(node: ASTNode) = true
}