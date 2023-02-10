import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {BookService} from "../../../shared/services/book.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-add-book',
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.css']
})
export class AddBookComponent implements OnInit {
  public bookForm: FormGroup;

  constructor(private fb: FormBuilder,
              private bookService: BookService,
              private dialogRef: MatDialogRef<AddBookComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {

    this.bookForm = this.fb.group({
        url: ['', Validators.required]
      }
    );

  }

  ngOnInit(): void {
    console.log(this.data);
  }

  onSave() {
    let data = {
      url: this.bookForm.get('url')?.value
    }

    this.bookService.createBook(data).subscribe(data => {
      console.log(data);
      this.dialogRef.close(1);
    }, error => {
      console.log(error);
      this.dialogRef.close(2);
    });
  }

  onCancel() {
    this.dialogRef.close(3);
  }
}
