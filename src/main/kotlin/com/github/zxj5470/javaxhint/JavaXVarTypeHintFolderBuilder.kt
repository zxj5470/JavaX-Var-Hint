package com.github.zxj5470.javaxhint

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.Document
import com.intellij.psi.*

/**
 * @author: zxj5470
 * @date: 2018/3/23
 */
class JavaXVarTypeHintFolderBuilder : FoldingBuilderEx() {
	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
		val ret = ArrayList<FoldingDescriptor>()
		if(root.hasNoError)
		root.children.filterIsInstance<PsiClass>().forEach {
			it.children.filterIsInstance<PsiMethod>().forEach {
				it.children.first { it is PsiCodeBlock }
					.children.forEach {
					findVarDeclaration(it).let { (bool, psi, text) ->
						if (bool) {
							ret.add(getFold(psi!!, text!!))
						}
					}
				}
			}
		}
		return ret.toTypedArray()
	}

	private fun getFold(elem: PsiElement, placeHolder: String) =
		object : FoldingDescriptor(elem, elem.textRange) {
			override fun getPlaceholderText(): String? {
				return placeHolder
			}
		}

	override fun getPlaceholderText(node: ASTNode) = "..."

	override fun isCollapsedByDefault(node: ASTNode) = true
}