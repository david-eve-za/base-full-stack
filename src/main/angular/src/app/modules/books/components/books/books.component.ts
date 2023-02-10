import {Component, OnInit, ViewChild} from '@angular/core';
import {UtilService} from "../../../shared/services/util.service";
import {MatTableDataSource} from "@angular/material/table";
import {BookService} from "../../../shared/services/book.service";
import {MatPaginator} from "@angular/material/paginator";
import {MatDialog} from "@angular/material/dialog";
import {AddBookComponent} from "../add-book/add-book.component";
import {ActivatedRoute, Router, RouterModule} from "@angular/router";

export interface BookElement {
  id: number;
  title: string;
  chapters: number;
  unread: number;
}

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.css']
})
export class BooksComponent implements OnInit {
  displayedColumns: string[] = ['id', 'title', 'chapters','unread', 'actions'];
  dataSource = new MatTableDataSource<BookElement>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;
  isAdmin!: boolean;

  constructor(private utils: UtilService,
              private bookService:BookService,
              private dialog: MatDialog,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
    // setInterval(this.getBooks,60000);
  }

  ngOnInit(): void {
    this.isAdmin = this.utils.isAdmin();
    this.getBooks();

  }

  findBy(value: string) {
    this.dataSource.filter = value.trim().toLowerCase();
  }

  private getBooks() {
    this.bookService.getBooks().subscribe(data => {
      console.log(data);
      this.processBookData(data);
    }, error => {
      console.log(error);
    });
  }

  private processBookData(data: any) {
    const books: BookElement[] = [];
    if (data.metadata[0].code == "200") {
      data.data.forEach((book: any) => {
        books.push({
          id: book.id,
          title: book.title != null ? book.title.toUpperCase() : "TBD",
          chapters: book.chapters.length,
          unread: book.chapters.filter((chapter: any) => chapter.read == false).length
        });
      });
      this.dataSource = new MatTableDataSource<BookElement>(books);
      this.dataSource.paginator = this.paginator;
    }
  }

  openAddBookDialog() {
    const dialogRef = this.dialog.open(AddBookComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getBooks();
    });
  }

  refreshBook(id:number) {
    this.bookService.getMetadata(id).subscribe(data=> {
      this.getBooks();
    },error => {
      console.log(error);
    });
  }

  showChapters() {
    console.log("showChapters");
    try {
      this.router.navigate(['chapters'], {relativeTo: this.activatedRoute});
    }
    catch (e) {
      console.log(e);
    }
  }
}
