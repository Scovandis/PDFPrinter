package com.dxy.pondok.pdfprinter

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.dxy.pondok.pdfprinter.databinding.ActivityMainBinding

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class MainActivity : AppCompatActivity() {
  lateinit var binding : ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    Dexter.withContext(this)
      .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
      .withListener(object : PermissionListener {
        override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
          binding.printPDF.setOnClickListener { createPDFFile(Comman.getAppPath(this@MainActivity) + "test_pdf.pdf") }
        }

        override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
        override fun onPermissionRationaleShouldBeShown(
          permissionRequest: PermissionRequest?,
          permissionToken: PermissionToken?
        ) {
        }
      })
      .check()
  }

  private fun createPDFFile(path: String) {
    val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    if (File(path).exists()) File(path).delete()
    try {
      val document = Document()
      //Save
      PdfWriter.getInstance(document, FileOutputStream(path))
      //open to write
      document.open()
      //Settings
      document.pageSize = PageSize.A4
      document.addCreationDate()
      document.addAuthor("Dxy")
      document.addCreator("gondres jare")

      //Font Settings
      val colorAccent = BaseColor(0, 153, 204, 255)
      val fontSizeHeader = 20.0f
      val fontSizeBody = 16.0f
      val valueFontSize = 26.0f

      //Custom font
      val fontName = BaseFont.createFont("res/font/robotobold.ttf", "UTF-8", BaseFont.EMBEDDED)

      //create title of document
      val titleFont = Font(fontName, 20.0f, Font.NORMAL, BaseColor.BLACK)
      addNewItem(document, "Order Deatils", Element.ALIGN_CENTER, titleFont)

      // Add more
      val orderNumberFont = Font(fontName, fontSizeHeader, Font.NORMAL, colorAccent)
      addNewItem(document, "order number", Element.ALIGN_LEFT, orderNumberFont)
      val orderNumberValueFont = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)
      addNewItem(document, "#525263", Element.ALIGN_LEFT, orderNumberValueFont)
      addLineSeperator(document)
      addNewItem(document, "Order Date", Element.ALIGN_LEFT, orderNumberFont)
      addNewItem(document, date, Element.ALIGN_LEFT, orderNumberValueFont)
      addLineSeperator(document)
      addNewItem(document, "Account name", Element.ALIGN_LEFT, orderNumberFont)
      addNewItem(document, "andre", Element.ALIGN_LEFT, orderNumberValueFont)
      addLineSeperator(document)

      //Add product order detail
      addLineSpace(document)
      addNewItem(document, "Product details", Element.ALIGN_CENTER, titleFont)
      addLineSeperator(document)

      //item 1
      addNewItemWithLeftAndRight(document, "pentol kabul", "(1.0%)", titleFont, orderNumberValueFont)
      addNewItemWithLeftAndRight(document, "20", "1200.0", titleFont, orderNumberValueFont)
      addLineSeperator(document)

      //item 2
      addNewItemWithLeftAndRight(document, "siomay", "(0.0%)", titleFont, orderNumberValueFont)
      addNewItemWithLeftAndRight(document, "12", "1520.0", titleFont, orderNumberValueFont)
      addLineSeperator(document)

      //item 3
      addNewItemWithLeftAndRight(document, "Ote-ote", "(0.0%)", titleFont, orderNumberValueFont)
      addNewItemWithLeftAndRight(document, "10", "1000.0", titleFont, orderNumberValueFont)
      addLineSeperator(document)

      //Total
      addLineSpace(document)
      addLineSpace(document)
      addNewItemWithLeftAndRight(document, "total", "8500", titleFont, orderNumberValueFont)
      document.close()
      Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show()
      printPDF()
    } catch (e: FileNotFoundException) {
      e.printStackTrace()
    } catch (e: DocumentException) {
      e.printStackTrace()
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }

  @Throws(DocumentException::class)
  private fun addNewItem(document: Document, text: String, align: Int, font: Font) {
    val chunk = Chunk(text, font)
    val paragraph = Paragraph(chunk)
    paragraph.alignment = align
    document.add(paragraph)
  }

  @Throws(DocumentException::class)
  private fun addNewItemWithLeftAndRight(
    document: Document,
    textLeft: String,
    textRight: String,
    textLeftFont: Font,
    textRightFont: Font
  ) {
    val chunkTextLeft = Chunk(textLeft, textLeftFont)
    val chunkTextRight = Chunk(textRight, textRightFont)
    val p = Paragraph(chunkTextLeft)
    p.add(Chunk(VerticalPositionMark()))
    p.add(chunkTextRight)
    document.add(p)
  }

  @Throws(DocumentException::class)
  private fun addLineSeperator(document: Document) {
    val lineSeparator = LineSeparator()
    lineSeparator.lineColor = BaseColor(0, 0, 0, 68)
    addLineSpace(document)
    document.add(Chunk(lineSeparator))
  }

  @Throws(DocumentException::class)
  private fun addLineSpace(document: Document) {
    document.add(Paragraph(""))
  }

  private fun printPDF() {
    val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
    try {
      val printDocumentAdapter: PrintDocumentAdapter =
        PdfDocumentAdapter(this@MainActivity, Comman.getAppPath(this@MainActivity) + "test_pdf.pdf")
      printManager.print("Document", printDocumentAdapter, PrintAttributes.Builder().build())
    } catch (ex: Exception) {
      Log.e("Andre", "" + ex.message)
      Toast.makeText(this@MainActivity, "Can't read pdf file", Toast.LENGTH_SHORT).show()
    }
  }
}