import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BooksComponent } from './components/books/books.component';
import {MaterialModule} from "../shared/material.module";
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AddBookComponent } from './components/add-book/add-book.component';
import { ChaptersComponent } from './components/chapters/chapters.component';
import {RouterModule} from "@angular/router";



@NgModule({
  declarations: [
    BooksComponent,
    AddBookComponent,
    ChaptersComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FlexLayoutModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class BooksModule { }
