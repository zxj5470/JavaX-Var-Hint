package com.github.zxj5470.javaxhint

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author: zxj5470
 * @date: 2018/3/22
 */
fun findVarDeclaration(element: PsiElement): Triple<Boolean, PsiElement?, String?> =
	when (element) {
		is PsiDeclarationStatement -> {
			var boolean = false
			// no blank before and after typeText
			var typeText: String? = null
			var elem: PsiElement? = null
			element.declaredElements.first().let {
				it.children
					.filter { it is PsiIdentifier || it is PsiTypeElement || it.typeFilter }
					.forEach {
						when (it) {
							is PsiTypeElement, is PsiJavaCodeReferenceElement -> {
								if (it.text == "var") {
									boolean = true
								}
							}
							is PsiIdentifier -> {
								if (boolean)
									elem = it
							}
							is PsiNewExpression -> {
								typeText = it.type?.presentableText
								if (it.children.any { it is PsiAnonymousClass }) {
									typeText = "< ? extends $typeText >"
								}
							}
							is PsiLiteralExpression -> {
								typeText = it.type?.presentableText
							}
							is PsiMethodCallExpression -> {
								typeText = it.type?.presentableText
							}
						}
					}
				if (boolean) {
					elem?.nextSibling?.let {
						if (it.text == "=") {
							elem = elem?.nextSibling
							typeText = " : $typeText ="
						} else {
							elem = elem?.nextSibling
							typeText = " : $typeText "
						}
					}
				}
				Triple(boolean, elem, typeText)
			}
		}
		else -> Triple(false, null, null)
	}

val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)

val PsiElement.typeFilter
	get() =
		this is PsiJavaCodeReferenceElement
			|| this is PsiNewExpression
			|| this is PsiLiteralExpression
			|| this is PsiMethodCallExpression